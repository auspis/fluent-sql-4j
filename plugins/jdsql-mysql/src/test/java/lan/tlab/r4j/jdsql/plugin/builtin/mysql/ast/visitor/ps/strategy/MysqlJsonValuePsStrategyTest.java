package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.core.expression.function.json.BehaviorKind;
import lan.tlab.r4j.jdsql.ast.core.expression.function.json.JsonValue;
import lan.tlab.r4j.jdsql.ast.core.expression.function.json.OnEmptyBehavior;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.Test;

class MysqlJsonValuePsStrategyTest {

    @Test
    void withBasicArguments() {
        PreparedStatementRenderer specFactory = PreparedStatementRenderer.builder()
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
        PreparedStatementRenderer specFactory = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonValuePsStrategy strategy = new MysqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"));

        PreparedStatementSpec result = strategy.handle(jsonValue, specFactory, new AstContext());

        // PreparedStatementRenderer outputs only column name when no table context
        assertThat(result.sql()).isEqualTo("JSON_UNQUOTE(JSON_EXTRACT(`data`, ?))");
        assertThat(result.parameters()).containsExactly("$.price");
    }

    @Test
    void withReturningTypeIsIgnored() {
        // MySQL does not support RETURNING - it should be ignored
        PreparedStatementRenderer specFactory = PreparedStatementRenderer.builder()
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
        PreparedStatementRenderer specFactory = PreparedStatementRenderer.builder()
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
        PreparedStatementRenderer specFactory = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonValuePsStrategy strategy = new MysqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(ColumnReference.of("orders", "metadata"), Literal.of("$.customer.name"));

        PreparedStatementSpec result = strategy.handle(jsonValue, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_UNQUOTE(JSON_EXTRACT(`metadata`, ?))");
        assertThat(result.parameters()).containsExactly("$.customer.name");
    }
}
