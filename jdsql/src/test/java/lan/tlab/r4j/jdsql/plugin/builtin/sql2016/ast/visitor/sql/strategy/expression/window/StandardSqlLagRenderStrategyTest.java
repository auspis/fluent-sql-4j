package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.Lag;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.OverClause;
import lan.tlab.r4j.jdsql.ast.dql.clause.Sorting;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlLagRenderStrategyTest {

    private SqlRenderer sqlRenderer;
    private StandardSqlLagRenderStrategy strategy;

    @BeforeEach
    public void setUp() {
        sqlRenderer = TestDialectRendererFactory.standardSql();
        strategy = new StandardSqlLagRenderStrategy();
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
