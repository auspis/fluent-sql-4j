package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.BehaviorKind;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonValue;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.OnEmptyBehavior;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.escape.MysqlEscapeStrategy;
import org.junit.jupiter.api.Test;

class MysqlJsonValuePsStrategyTest {

    @Test
    void withBasicArguments() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonValuePsStrategy strategy = new MysqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(ColumnReference.of("users", "address"), Literal.of("$.city"));

        PsDto result = strategy.handle(jsonValue, renderer, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_UNQUOTE(JSON_EXTRACT(`address`, ?))");
        assertThat(result.parameters()).containsExactly("$.city");
    }

    @Test
    void withTableQualifiedColumn() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonValuePsStrategy strategy = new MysqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"));

        PsDto result = strategy.handle(jsonValue, renderer, new AstContext());

        // PreparedStatementRenderer outputs only column name when no table context
        assertThat(result.sql()).isEqualTo("JSON_UNQUOTE(JSON_EXTRACT(`data`, ?))");
        assertThat(result.parameters()).containsExactly("$.price");
    }

    @Test
    void withReturningTypeIsIgnored() {
        // MySQL does not support RETURNING - it should be ignored
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonValuePsStrategy strategy = new MysqlJsonValuePsStrategy();
        JsonValue jsonValue =
                new JsonValue(ColumnReference.of("products", "price"), Literal.of("$.amount"), "DECIMAL(10,2)");

        PsDto result = strategy.handle(jsonValue, renderer, new AstContext());

        // RETURNING type should be ignored
        assertThat(result.sql()).isEqualTo("JSON_UNQUOTE(JSON_EXTRACT(`price`, ?))");
        assertThat(result.parameters()).containsExactly("$.amount");
    }

    @Test
    void withOnEmptyAndOnErrorBehaviorsAreIgnored() {
        // MySQL does not support ON EMPTY or ON ERROR - they should be ignored
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonValuePsStrategy strategy = new MysqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(
                ColumnReference.of("products", "data"),
                Literal.of("$.discount"),
                "DECIMAL(10,2)",
                OnEmptyBehavior.defaultValue("0.00"),
                BehaviorKind.ERROR);

        PsDto result = strategy.handle(jsonValue, renderer, new AstContext());

        // ON EMPTY and ON ERROR should be ignored
        assertThat(result.sql()).isEqualTo("JSON_UNQUOTE(JSON_EXTRACT(`data`, ?))");
        assertThat(result.parameters()).containsExactly("$.discount");
    }

    @Test
    void withNestedJsonPath() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonValuePsStrategy strategy = new MysqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(ColumnReference.of("orders", "metadata"), Literal.of("$.customer.name"));

        PsDto result = strategy.handle(jsonValue, renderer, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_UNQUOTE(JSON_EXTRACT(`metadata`, ?))");
        assertThat(result.parameters()).containsExactly("$.customer.name");
    }
}
