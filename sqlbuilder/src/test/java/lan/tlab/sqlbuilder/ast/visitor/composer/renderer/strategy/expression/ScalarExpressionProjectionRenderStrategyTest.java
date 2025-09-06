package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.convert.Cast;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScalarExpressionProjectionRenderStrategyTest {

    private ScalarExpressionProjectionRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new ScalarExpressionProjectionRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void columnReference() {
        ScalarExpressionProjection projection = new ScalarExpressionProjection(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(projection, sqlRenderer);
        assertThat(sql).isEqualTo("\"Customer\".\"name\"");
    }

    @Test
    void literalWithAlias() {
        ScalarExpressionProjection projection = new ScalarExpressionProjection(Literal.of(23), "age");
        String sql = strategy.render(projection, sqlRenderer);
        assertThat(sql).isEqualTo("23 AS age");
    }

    @Test
    void castColumnToVarchar() {
        ScalarExpressionProjection projection =
                new ScalarExpressionProjection(Cast.of(ColumnReference.of("Customer", "id"), "VARCHAR(255)"), "strId");
        String sql = strategy.render(projection, sqlRenderer);
        assertThat(sql).isEqualTo("CAST(\"Customer\".\"id\" AS VARCHAR(255)) AS strId");
    }
}
