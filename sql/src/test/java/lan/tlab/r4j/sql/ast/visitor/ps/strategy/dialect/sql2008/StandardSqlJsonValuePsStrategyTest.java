package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.BehaviorKind;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonValue;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.OnEmptyBehavior;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.JsonValuePsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlJsonValuePsStrategyTest {

    @Test
    void withBasicArguments() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder().build();
        JsonValuePsStrategy strategy = new StandardSqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"));

        PsDto result = strategy.handle(jsonValue, renderer, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_VALUE(\"data\", ?)");
        assertThat(result.parameters()).containsExactly("$.price");
    }

    @Test
    void withAllOptions() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder().build();
        JsonValuePsStrategy strategy = new StandardSqlJsonValuePsStrategy();
        JsonValue jsonValue = new JsonValue(
                ColumnReference.of("products", "data"),
                Literal.of("$.price"),
                "DECIMAL(10,2)",
                OnEmptyBehavior.defaultValue("0.0"),
                BehaviorKind.NONE);

        PsDto result = strategy.handle(jsonValue, renderer, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_VALUE(\"data\", ? RETURNING DECIMAL(10,2) DEFAULT 0.0 ON EMPTY)");
        assertThat(result.parameters()).containsExactly("$.price");
    }
}
