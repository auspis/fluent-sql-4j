package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.In;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InRenderStrategyTest {

    private InRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new InRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void numbers() {
        In exp = new In(
                ColumnReference.of("Customer", "score"), List.of(Literal.of(100), Literal.of(200), Literal.of(300)));

        String sql = strategy.render(exp, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\".\"score\" IN(100, 200, 300)");
    }

    @Test
    void strings() {
        In exp = new In(
                ColumnReference.of("Customer", "name"), Literal.of("john0"), Literal.of("john1"), Literal.of("john2"));

        String sql = strategy.render(exp, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\".\"name\" IN('john0', 'john1', 'john2')");
    }
}
