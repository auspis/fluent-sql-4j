package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.BehaviorKind;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonValue;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.OnEmptyBehavior;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.JsonValuePsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlJsonValuePsStrategyTest {

    @Test
    void withBasicArguments() {
        PreparedStatementRenderer specFactory =
                PreparedStatementRenderer.builder().build();
        JsonValuePsStrategy strategy = new StandardSqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"));

        PreparedStatementSpec result = strategy.handle(jsonValue, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_VALUE(\"data\", ?)");
        assertThat(result.parameters()).containsExactly("$.price");
    }

    @Test
    void withAllOptions() {
        PreparedStatementRenderer specFactory =
                PreparedStatementRenderer.builder().build();
        JsonValuePsStrategy strategy = new StandardSqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(
                ColumnReference.of("products", "data"),
                Literal.of("$.price"),
                "DECIMAL(10,2)",
                OnEmptyBehavior.defaultValue("0.0"),
                BehaviorKind.NONE);

        PreparedStatementSpec result = strategy.handle(jsonValue, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_VALUE(\"data\", ? RETURNING DECIMAL(10,2) DEFAULT 0.0 ON EMPTY)");
        assertThat(result.parameters()).containsExactly("$.price");
    }
}
