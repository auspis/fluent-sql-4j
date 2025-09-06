package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.bool.Like;
import lan.tlab.sqlbuilder.ast.expression.bool.logical.Not;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotRenderStrategyTest {

    private NotRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new NotRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        Not exp = new Not(new Like(ColumnReference.of("Customer", "name"), "A%"));
        String sql = strategy.render(exp, sqlRenderer);
        assertThat(sql).isEqualTo("NOT (\"Customer\".\"name\" LIKE 'A%')");
    }
}
