package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.clause.orderby.Sorting;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Ntile;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.OverClause;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NtileRenderStrategyTest {

    private SqlRenderer sqlRenderer;
    private NtileRenderStrategy strategy;

    @BeforeEach
    public void setUp() {
        sqlRenderer = TestDialectRendererFactory.standardSql2008();
        strategy = new NtileRenderStrategy();
    }

    @Test
    void rendersNtileWithOverClauseOrderBy() {
        Ntile ntile = new Ntile(
                4,
                OverClause.builder()
                        .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                        .build());
        String sql = strategy.render(ntile, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("NTILE(4) OVER (ORDER BY \"Employee\".\"salary\" DESC)");
    }

    @Test
    void rendersNtileWithPartitionByAndOrderBy() {
        Ntile ntile = new Ntile(
                4,
                OverClause.builder()
                        .partitionBy(List.of(ColumnReference.of("Employee", "department")))
                        .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                        .build());
        String sql = strategy.render(ntile, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "NTILE(4) OVER (PARTITION BY \"Employee\".\"department\" ORDER BY \"Employee\".\"salary\" DESC)");
    }
}
