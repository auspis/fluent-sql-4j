package lan.tlab.r4j.jdsql.plugin.builtin.postgre.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonExists;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.JsonExistsRenderStrategy;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.Test;

class PostgreSqlJsonExistsRenderStrategyTest {

    // TODO: use postgre sql renderer
    @Test
    void ok() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql();
        JsonExistsRenderStrategy strategy = new PostgreJsonExistsRenderStrategy();
        JsonExists jsonExists = new JsonExists(ColumnReference.of("products", "data"), Literal.of("$.price"));
        String sql = strategy.render(jsonExists, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("JSON_EXISTS(\"products\".\"data\", '$.price')");
    }
}
