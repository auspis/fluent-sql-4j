package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.BehaviorKind;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.JsonExists;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlJsonExistsPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlJsonExistsPsStrategyTest {

    @Test
    void withBasicArguments() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder().build();
        StandardSqlJsonExistsPsStrategy strategy = new StandardSqlJsonExistsPsStrategy();
        JsonExists jsonExists = new JsonExists(ColumnReference.of("products", "data"), Literal.of("$.price"));

        PsDto result = strategy.handle(jsonExists, renderer, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_EXISTS(\"data\", ?)");
        assertThat(result.parameters()).containsExactly("$.price");
    }

    @Test
    void withOnErrorBehavior() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder().build();
        StandardSqlJsonExistsPsStrategy strategy = new StandardSqlJsonExistsPsStrategy();
        JsonExists jsonExists =
                new JsonExists(ColumnReference.of("products", "data"), Literal.of("$.price"), BehaviorKind.ERROR);

        PsDto result = strategy.handle(jsonExists, renderer, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_EXISTS(\"data\", ? ERROR ON ERROR)");
        assertThat(result.parameters()).containsExactly("$.price");
    }
}
