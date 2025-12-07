package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.BehaviorKind;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonExists;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.escape.MysqlEscapeStrategy;
import org.junit.jupiter.api.Test;

class MysqlJsonExistsPsStrategyTest {

    @Test
    void withBasicArguments() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonExistsPsStrategy strategy = new MysqlJsonExistsPsStrategy();
        JsonExists jsonExists = new JsonExists(ColumnReference.of("users", "address"), Literal.of("$.city"));

        PsDto result = strategy.handle(jsonExists, renderer, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_CONTAINS_PATH(`address`, 'one', ?)");
        assertThat(result.parameters()).containsExactly("$.city");
    }

    @Test
    void withTableQualifiedColumn() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonExistsPsStrategy strategy = new MysqlJsonExistsPsStrategy();
        JsonExists jsonExists = new JsonExists(ColumnReference.of("users", "data"), Literal.of("$.email"));

        PsDto result = strategy.handle(jsonExists, renderer, new AstContext());

        // PreparedStatementRenderer outputs only column name when no table context
        assertThat(result.sql()).isEqualTo("JSON_CONTAINS_PATH(`data`, 'one', ?)");
        assertThat(result.parameters()).containsExactly("$.email");
    }

    @Test
    void withOnErrorBehaviorIsIgnored() {
        // MySQL does not support ON ERROR - it should be ignored
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonExistsPsStrategy strategy = new MysqlJsonExistsPsStrategy();
        JsonExists jsonExists =
                new JsonExists(ColumnReference.of("products", "data"), Literal.of("$.price"), BehaviorKind.ERROR);

        PsDto result = strategy.handle(jsonExists, renderer, new AstContext());

        // ON ERROR behavior should be ignored - only JSON_CONTAINS_PATH syntax
        assertThat(result.sql()).isEqualTo("JSON_CONTAINS_PATH(`data`, 'one', ?)");
        assertThat(result.parameters()).containsExactly("$.price");
    }

    @Test
    void withComplexJsonPath() {
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonExistsPsStrategy strategy = new MysqlJsonExistsPsStrategy();
        JsonExists jsonExists = new JsonExists(ColumnReference.of("orders", "metadata"), Literal.of("$.items[0].id"));

        PsDto result = strategy.handle(jsonExists, renderer, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_CONTAINS_PATH(`metadata`, 'one', ?)");
        assertThat(result.parameters()).containsExactly("$.items[0].id");
    }
}
