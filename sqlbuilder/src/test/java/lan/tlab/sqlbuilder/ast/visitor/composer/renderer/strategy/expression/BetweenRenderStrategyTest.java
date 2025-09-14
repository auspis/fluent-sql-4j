package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import lan.tlab.sqlbuilder.ast.expression.bool.Between;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BetweenRenderStrategyTest {

    private BetweenRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new BetweenRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void numbers() {
        Between exp = new Between(ColumnReference.of("Customer", "score"), Literal.of(400), Literal.of(405.02));

        String sql = strategy.render(exp, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(\"Customer\".\"score\" BETWEEN 400 AND 405.02)");
    }

    @Test
    void dates() {
        Between exp = new Between(
                ColumnReference.of("Customer", "birthdate"),
                Literal.of(LocalDate.of(2000, 2, 3)),
                Literal.of(LocalDate.of(2000, 2, 4)));

        String sql = strategy.render(exp, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(\"Customer\".\"birthdate\" BETWEEN '2000-02-03' AND '2000-02-04')");
    }
}
