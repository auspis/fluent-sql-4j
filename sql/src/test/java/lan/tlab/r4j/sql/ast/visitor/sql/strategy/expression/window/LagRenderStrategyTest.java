package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.clause.orderby.Sorting;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Lag;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.OverClause;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LagRenderStrategyTest {

    private SqlRenderer sqlRenderer;
    private LagRenderStrategy strategy;

    @BeforeEach
    public void setUp() {
        sqlRenderer = TestDialectRendererFactory.standardSql2008();
        strategy = new LagRenderStrategy();
    }

    @Test
    void rendersLagWithOverClauseOrderBy() {
        Lag lag = new Lag(
                ColumnReference.of("Employee", "salary"),
                1,
                null,
                OverClause.builder()
                        .orderBy(List.of(Sorting.asc(ColumnReference.of("Employee", "hire_date"))))
                        .build());
        String sql = strategy.render(lag, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LAG(\"Employee\".\"salary\", 1) OVER (ORDER BY \"Employee\".\"hire_date\" ASC)");
    }

    @Test
    void rendersLagWithDefaultValueAndOverClause() {
        Lag lag = new Lag(
                ColumnReference.of("Employee", "salary"),
                1,
                Literal.of(0),
                OverClause.builder()
                        .orderBy(List.of(Sorting.asc(ColumnReference.of("Employee", "hire_date"))))
                        .build());
        String sql = strategy.render(lag, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LAG(\"Employee\".\"salary\", 1, 0) OVER (ORDER BY \"Employee\".\"hire_date\" ASC)");
    }
}
