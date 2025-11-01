package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonExists;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.OnErrorBehavior;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultJsonExistsPsStrategyTest {

    @Test
    void withBasicArguments() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder().build();
        DefaultJsonExistsPsStrategy strategy = new DefaultJsonExistsPsStrategy();
        JsonExists jsonExists = new JsonExists(ColumnReference.of("products", "data"), Literal.of("$.price"));

        PsDto result = strategy.handle(jsonExists, renderer, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_EXISTS(\"data\", ?)");
        assertThat(result.parameters()).containsExactly("$.price");
    }

    @Test
    void withOnErrorBehavior() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder().build();
        DefaultJsonExistsPsStrategy strategy = new DefaultJsonExistsPsStrategy();
        JsonExists jsonExists =
                new JsonExists(ColumnReference.of("products", "data"), Literal.of("$.price"), OnErrorBehavior.error());

        PsDto result = strategy.handle(jsonExists, renderer, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_EXISTS(\"data\", ? ERROR ON ERROR)");
        assertThat(result.parameters()).containsExactly("$.price");
    }
}
