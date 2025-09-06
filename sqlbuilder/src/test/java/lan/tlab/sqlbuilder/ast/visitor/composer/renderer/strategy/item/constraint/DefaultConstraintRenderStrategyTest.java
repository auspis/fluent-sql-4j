package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.constraint;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.DefaultConstraint;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;

class DefaultConstraintRenderStrategyTest {

    private DefaultConstraintRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new DefaultConstraintRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void string() {
        DefaultConstraint constraint = new DefaultConstraint(Literal.of("def-val"));
        String sql = strategy.render(constraint, renderer);
        assertThat(sql).isEqualTo("DEFAULT 'def-val'");
    }
    
    @Test
    void number() {
        DefaultConstraint constraint = new DefaultConstraint(Literal.of(42));
        String sql = strategy.render(constraint, renderer);
        assertThat(sql).isEqualTo("DEFAULT 42");
    }
}