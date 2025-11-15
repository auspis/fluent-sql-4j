package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.CharLength;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.CharLengthRenderStrategy;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.Test;

class StandardSqlCharLengthRenderStrategyTest {

    @Test
    void ok() {
        SqlRenderer sqlRenderer = StandardSqlRendererFactory.standardSql();
        CharLengthRenderStrategy strategy = new StandardSqlCharLengthRenderStrategy();
        CharLength fun = new CharLength(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(fun, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CHAR_LENGTH(\"Customer\".\"name\")");
    }
}
