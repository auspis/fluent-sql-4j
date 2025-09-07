package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll.constraint;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.NotNullConstraint;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll.constraint.NotNullConstraintRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotNullConstraintRenderStrategyTest {

    private NotNullConstraintRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new NotNullConstraintRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        NotNullConstraint constraint = new NotNullConstraint();
        String sql = strategy.render(constraint, renderer);
        assertThat(sql).isEqualTo("NOT NULL");
    }
}
