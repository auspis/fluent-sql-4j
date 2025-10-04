package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.dll.constraint;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.Constraint.CheckConstraint;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Comparison expr = Comparison.gt(ColumnReference.of("", "age"), Literal.of(18));
        CheckConstraint constraint = new CheckConstraint(expr);
        String sql = strategy.render(constraint, renderer, new AstContext());
        assertThat(sql).isEqualTo("CHECK (\"age\" > 18)");
    }
}
