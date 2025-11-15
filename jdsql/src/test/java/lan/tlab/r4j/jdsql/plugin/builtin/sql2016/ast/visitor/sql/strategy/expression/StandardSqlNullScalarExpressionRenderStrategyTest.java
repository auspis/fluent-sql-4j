package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.NullScalarExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlNullScalarExpressionRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlNullScalarExpressionRenderStrategyTest {

    private StandardSqlNullScalarExpressionRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlNullScalarExpressionRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void ok() {
        NullScalarExpression func = new NullScalarExpression();
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("");
    }
}
