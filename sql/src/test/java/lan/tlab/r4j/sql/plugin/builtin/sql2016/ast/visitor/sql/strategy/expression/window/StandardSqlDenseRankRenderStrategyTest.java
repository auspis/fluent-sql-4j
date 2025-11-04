package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.clause.orderby.Sorting;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.DenseRank;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.OverClause;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlDenseRankRenderStrategyTest {

    private SqlRenderer sqlRenderer;
    private StandardSqlDenseRankRenderStrategy strategy;

    @BeforeEach
    public void setUp() {
        sqlRenderer = TestDialectRendererFactory.standardSql();
        strategy = new StandardSqlDenseRankRenderStrategy();
    }

    @Test
    void rendersDenseRankWithOverClauseOrderBy() {
        DenseRank denseRank = new DenseRank(OverClause.builder()
                .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                .build());
        String sql = strategy.render(denseRank, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("DENSE_RANK() OVER (ORDER BY \"Employee\".\"salary\" DESC)");
    }

    @Test
    void rendersDenseRankWithPartitionByAndOrderBy() {
        DenseRank denseRank = new DenseRank(OverClause.builder()
                .partitionBy(List.of(ColumnReference.of("Employee", "department")))
                .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                .build());
        String sql = strategy.render(denseRank, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "DENSE_RANK() OVER (PARTITION BY \"Employee\".\"department\" ORDER BY \"Employee\".\"salary\" DESC)");
    }
}
