package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.NullScalarExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NullScalarExpressionRenderStrategyTest {

    private NullScalarExpressionRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new NullScalarExpressionRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        NullScalarExpression func = new NullScalarExpression();
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("");
    }
}
