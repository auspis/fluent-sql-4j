package lan.tlab.r4j.jdsql.plugin.builtin.postgre.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonQuery;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.JsonQueryRenderStrategy;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.Test;

class PostgreJsonQueryRenderStrategyTest {

    // TODO: use postgre sql renderer
    @Test
    void ok() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql();
        JsonQueryRenderStrategy strategy = new PostgreJsonQueryRenderStrategy();
        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("products", "data"), Literal.of("$.tags"));
        String sql = strategy.render(jsonQuery, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("JSON_QUERY(\"products\".\"data\", '$.tags')");
    }
}
