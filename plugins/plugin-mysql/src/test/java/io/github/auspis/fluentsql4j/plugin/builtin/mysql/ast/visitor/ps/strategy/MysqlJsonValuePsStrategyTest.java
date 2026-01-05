package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.function.json.BehaviorKind;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.JsonValue;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.OnEmptyBehavior;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.Test;

class MysqlJsonValuePsStrategyTest {

    @Test
    void withBasicArguments() {
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonValuePsStrategy strategy = new MysqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(ColumnReference.of("users", "address"), Literal.of("$.city"));

        PreparedStatementSpec result = strategy.handle(jsonValue, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_UNQUOTE(JSON_EXTRACT(`address`, ?))");
        assertThat(result.parameters()).containsExactly("$.city");
    }

    @Test
    void withTableQualifiedColumn() {
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonValuePsStrategy strategy = new MysqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"));

        PreparedStatementSpec result = strategy.handle(jsonValue, specFactory, new AstContext());

        // AstToPreparedStatementSpecVisitor outputs only column name when no table context
        assertThat(result.sql()).isEqualTo("JSON_UNQUOTE(JSON_EXTRACT(`data`, ?))");
        assertThat(result.parameters()).containsExactly("$.price");
    }

    @Test
    void withReturningTypeIsIgnored() {
        // MySQL does not support RETURNING - it should be ignored
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonValuePsStrategy strategy = new MysqlJsonValuePsStrategy();
        JsonValue jsonValue =
                new JsonValue(ColumnReference.of("products", "price"), Literal.of("$.amount"), "DECIMAL(10,2)");

        PreparedStatementSpec result = strategy.handle(jsonValue, specFactory, new AstContext());

        // RETURNING type should be ignored
        assertThat(result.sql()).isEqualTo("JSON_UNQUOTE(JSON_EXTRACT(`price`, ?))");
        assertThat(result.parameters()).containsExactly("$.amount");
    }

    @Test
    void withOnEmptyAndOnErrorBehaviorsAreIgnored() {
        // MySQL does not support ON EMPTY or ON ERROR - they should be ignored
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonValuePsStrategy strategy = new MysqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(
                ColumnReference.of("products", "data"),
                Literal.of("$.discount"),
                "DECIMAL(10,2)",
                OnEmptyBehavior.defaultValue("0.00"),
                BehaviorKind.ERROR);

        PreparedStatementSpec result = strategy.handle(jsonValue, specFactory, new AstContext());

        // ON EMPTY and ON ERROR should be ignored
        assertThat(result.sql()).isEqualTo("JSON_UNQUOTE(JSON_EXTRACT(`data`, ?))");
        assertThat(result.parameters()).containsExactly("$.discount");
    }

    @Test
    void withNestedJsonPath() {
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonValuePsStrategy strategy = new MysqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(ColumnReference.of("orders", "metadata"), Literal.of("$.customer.name"));

        PreparedStatementSpec result = strategy.handle(jsonValue, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_UNQUOTE(JSON_EXTRACT(`metadata`, ?))");
        assertThat(result.parameters()).containsExactly("$.customer.name");
    }
}
