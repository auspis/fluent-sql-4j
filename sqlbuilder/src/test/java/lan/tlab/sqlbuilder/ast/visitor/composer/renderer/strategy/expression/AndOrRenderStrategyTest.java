package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AndOrRenderStrategyTest {

    private AndOrRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new AndOrRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void and() {
        AndOr and = AndOr.and(
                Comparison.eq(ColumnReference.of("Customer", "name"), Literal.of("Jack")),
                Comparison.gt(ColumnReference.of("Customer", "age"), Literal.of(23)));
        String sql = strategy.render(and, sqlRenderer);
        assertThat(sql).isEqualTo("(\"Customer\".\"name\" = 'Jack') AND (\"Customer\".\"age\" > 23)");
    }

    @Test
    void and_empty() {
        AndOr and = AndOr.and();
        String sql = strategy.render(and, sqlRenderer);
        assertThat(sql).isEqualTo("");
    }

    @Test
    void and_oneBooleanExpression() {
        AndOr and = AndOr.and(Comparison.gt(ColumnReference.of("Customer", "age"), Literal.of(23)));
        String sql = strategy.render(and, sqlRenderer);
        assertThat(sql).isEqualTo("(\"Customer\".\"age\" > 23)");
    }

    @Test
    void or() {
        AndOr and = AndOr.or(
                Comparison.eq(ColumnReference.of("Customer", "name"), Literal.of("Jack")),
                Comparison.gt(ColumnReference.of("Customer", "age"), Literal.of(23)));
        String sql = strategy.render(and, sqlRenderer);
        assertThat(sql).isEqualTo("(\"Customer\".\"name\" = 'Jack') OR (\"Customer\".\"age\" > 23)");
    }
}
