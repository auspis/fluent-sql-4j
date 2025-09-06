package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.constraint;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.CheckConstraint;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;

class CheckConstraintRenderStrategyTest {

    private CheckConstraintRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new CheckConstraintRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        Comparison expr = Comparison.gt(
            ColumnReference.of("", "age"),
            Literal.of(18)
        );
        CheckConstraint constraint = new CheckConstraint(expr);
        String sql = strategy.render(constraint, renderer);
        assertThat(sql).isEqualTo("CHECK (\"age\" > 18)");
    }
}