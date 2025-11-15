package lan.tlab.r4j.jdsql.plugin.builtin.postgre.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonValue;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.JsonValueRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.Test;

class PostgreJsonValueRenderStrategyTest {

    // TODO: use postgre sql renderer
    @Test
    void ok() {
        SqlRenderer sqlRenderer = StandardSqlRendererFactory.standardSql();
        JsonValueRenderStrategy strategy = new PostgreJsonValueRenderStrategy();
        JsonValue jsonValue = new JsonValue(ColumnReference.of("products", "data"), Literal.of("$.price"));
        String sql = strategy.render(jsonValue, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("JSON_VALUE(\"products\".\"data\", '$.price')");
    }
}
