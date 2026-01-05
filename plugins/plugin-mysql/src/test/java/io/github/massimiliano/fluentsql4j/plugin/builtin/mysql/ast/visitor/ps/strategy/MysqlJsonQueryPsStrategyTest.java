package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.BehaviorKind;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.JsonQuery;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.OnEmptyBehavior;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.WrapperBehavior;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

class MysqlJsonQueryPsStrategyTest {

    @Test
    void withBasicArguments() {
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonQueryPsStrategy strategy = new MysqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("users", "address"), Literal.of("$"));

        PreparedStatementSpec result = strategy.handle(jsonQuery, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_EXTRACT(`address`, ?)");
        assertThat(result.parameters()).containsExactly("$");
    }

    @Test
    void withTableQualifiedColumn() {
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonQueryPsStrategy strategy = new MysqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("products", "data"), Literal.of("$.items"));

        PreparedStatementSpec result = strategy.handle(jsonQuery, specFactory, new AstContext());

        // AstToPreparedStatementSpecVisitor outputs only column name when no table context
        assertThat(result.sql()).isEqualTo("JSON_EXTRACT(`data`, ?)");
        assertThat(result.parameters()).containsExactly("$.items");
    }

    @Test
    void withReturningTypeIsIgnored() {
        // MySQL does not support RETURNING - it should be ignored
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonQueryPsStrategy strategy = new MysqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(
                ColumnReference.of("orders", "items"),
                Literal.of("$.products"),
                "JSON",
                WrapperBehavior.NONE,
                OnEmptyBehavior.returnNull(),
                BehaviorKind.NONE);

        PreparedStatementSpec result = strategy.handle(jsonQuery, specFactory, new AstContext());

        // RETURNING type should be ignored
        assertThat(result.sql()).isEqualTo("JSON_EXTRACT(`items`, ?)");
        assertThat(result.parameters()).containsExactly("$.products");
    }

    @Test
    void withWrapperBehaviorIsIgnored() {
        // MySQL does not support wrapper behavior - it should be ignored
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonQueryPsStrategy strategy = new MysqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(
                ColumnReference.of("users", "preferences"),
                Literal.of("$.tags"),
                null,
                WrapperBehavior.WITH_WRAPPER,
                OnEmptyBehavior.returnNull(),
                BehaviorKind.NONE);

        PreparedStatementSpec result = strategy.handle(jsonQuery, specFactory, new AstContext());

        // Wrapper behavior should be ignored
        assertThat(result.sql()).isEqualTo("JSON_EXTRACT(`preferences`, ?)");
        assertThat(result.parameters()).containsExactly("$.tags");
    }

    @Test
    void withAllOptionsIgnored() {
        // MySQL does not support RETURNING, wrapper, ON EMPTY, or ON ERROR
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonQueryPsStrategy strategy = new MysqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(
                ColumnReference.of("orders", "data"),
                Literal.of("$.history"),
                "JSON",
                WrapperBehavior.WITHOUT_WRAPPER,
                OnEmptyBehavior.defaultValue("{}"),
                BehaviorKind.ERROR);

        PreparedStatementSpec result = strategy.handle(jsonQuery, specFactory, new AstContext());

        // All SQL:2016 options should be ignored
        assertThat(result.sql()).isEqualTo("JSON_EXTRACT(`data`, ?)");
        assertThat(result.parameters()).containsExactly("$.history");
    }

    @Test
    void withArrayPath() {
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonQueryPsStrategy strategy = new MysqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("documents", "metadata"), Literal.of("$.tags[*]"));

        PreparedStatementSpec result = strategy.handle(jsonQuery, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_EXTRACT(`metadata`, ?)");
        assertThat(result.parameters()).containsExactly("$.tags[*]");
    }
}
