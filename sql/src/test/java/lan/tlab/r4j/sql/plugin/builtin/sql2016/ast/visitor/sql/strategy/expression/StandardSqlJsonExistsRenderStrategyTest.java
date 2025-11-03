package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.BehaviorKind;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonExists;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.JsonExistsRenderStrategy;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.Test;

class StandardSqlJsonExistsRenderStrategyTest {

    @Test
    void basicArguments() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql();
        JsonExistsRenderStrategy strategy = new StandardSqlJsonExistsRenderStrategy();
        JsonExists jsonExists = new JsonExists(ColumnReference.of("products", "data"), Literal.of("$.price"));
        String sql = strategy.render(jsonExists, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("JSON_EXISTS(\"products\".\"data\", '$.price')");
    }

    @Test
    void onErrorBehavior() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql();
        JsonExistsRenderStrategy strategy = new StandardSqlJsonExistsRenderStrategy();
        JsonExists jsonExists =
                new JsonExists(ColumnReference.of("products", "data"), Literal.of("$.price"), BehaviorKind.ERROR);
        String sql = strategy.render(jsonExists, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("JSON_EXISTS(\"products\".\"data\", '$.price' ERROR ON ERROR)");
    }
}
