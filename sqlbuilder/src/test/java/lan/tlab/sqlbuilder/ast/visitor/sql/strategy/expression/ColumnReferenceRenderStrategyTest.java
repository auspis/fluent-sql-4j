package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ColumnReferenceRenderStrategyTest {

    private ColumnReferenceRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new ColumnReferenceRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        ColumnReference columnReference = ColumnReference.of("Customer", "name");
        String sql = strategy.render(columnReference, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\".\"name\"");
    }

    @Test
    void star() {
        ColumnReference columnReference = ColumnReference.star();
        String sql = strategy.render(columnReference, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("*");
    }
}
