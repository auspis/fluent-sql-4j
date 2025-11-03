package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.clause.orderby.Sorting;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.OverClause;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Rank;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RankRenderStrategyTest {

    private SqlRenderer sqlRenderer;
    private RankRenderStrategy strategy;

    @BeforeEach
    public void setUp() {
        sqlRenderer = TestDialectRendererFactory.standardSql();
        strategy = new RankRenderStrategy();
    }

    @Test
    void rendersRankWithOverClauseOrderBy() {
        Rank rank = new Rank(OverClause.builder()
                .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                .build());
        String sql = strategy.render(rank, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("RANK() OVER (ORDER BY \"Employee\".\"salary\" DESC)");
    }

    @Test
    void rendersRankWithPartitionByAndOrderBy() {
        Rank rank = new Rank(OverClause.builder()
                .partitionBy(List.of(ColumnReference.of("Employee", "department")))
                .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                .build());
        String sql = strategy.render(rank, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "RANK() OVER (PARTITION BY \"Employee\".\"department\" ORDER BY \"Employee\".\"salary\" DESC)");
    }
}
