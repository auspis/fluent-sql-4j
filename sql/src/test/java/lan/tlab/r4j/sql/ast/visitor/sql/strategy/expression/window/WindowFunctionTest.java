package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.clause.orderby.Sorting;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.*;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WindowFunctionTest {

    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        sqlRenderer = TestDialectRendererFactory.standardSql2008();
    }

    @Test
    void rendersRowNumberWithOverClauseOrderBy() {
        RowNumber rowNumber = WindowFunction.rowNumber()
                .over(OverClause.builder()
                        .orderBy(List.of(Sorting.asc(ColumnReference.of("Employee", "salary"))))
                        .build());
        String sql = rowNumber.accept(sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("ROW_NUMBER() OVER (ORDER BY \"Employee\".\"salary\" ASC)");
    }

    @Test
    void rendersRowNumberWithOverClausePartitionByAndOrderBy() {
        RowNumber rowNumber = WindowFunction.rowNumber()
                .over(OverClause.builder()
                        .partitionBy(List.of(ColumnReference.of("Employee", "department")))
                        .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                        .build());
        String sql = rowNumber.accept(sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "ROW_NUMBER() OVER (PARTITION BY \"Employee\".\"department\" ORDER BY \"Employee\".\"salary\" DESC)");
    }

    @Test
    void rendersRankWithOverClauseOrderBy() {
        Rank rank = WindowFunction.rank()
                .over(OverClause.builder()
                        .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                        .build());
        String sql = rank.accept(sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("RANK() OVER (ORDER BY \"Employee\".\"salary\" DESC)");
    }

    @Test
    void rendersRankWithPartitionByAndOrderBy() {
        Rank rank = WindowFunction.rank()
                .over(OverClause.builder()
                        .partitionBy(List.of(ColumnReference.of("Employee", "department")))
                        .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                        .build());
        String sql = rank.accept(sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "RANK() OVER (PARTITION BY \"Employee\".\"department\" ORDER BY \"Employee\".\"salary\" DESC)");
    }

    @Test
    void rendersDenseRankWithOverClauseOrderBy() {
        DenseRank denseRank = WindowFunction.denseRank()
                .over(OverClause.builder()
                        .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                        .build());
        String sql = denseRank.accept(sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("DENSE_RANK() OVER (ORDER BY \"Employee\".\"salary\" DESC)");
    }

    @Test
    void rendersDenseRankWithPartitionByAndOrderBy() {
        DenseRank denseRank = WindowFunction.denseRank()
                .over(OverClause.builder()
                        .partitionBy(List.of(ColumnReference.of("Employee", "department")))
                        .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                        .build());
        String sql = denseRank.accept(sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "DENSE_RANK() OVER (PARTITION BY \"Employee\".\"department\" ORDER BY \"Employee\".\"salary\" DESC)");
    }

    @Test
    void rendersNtileWithOverClauseOrderBy() {
        Ntile ntile = WindowFunction.ntile(4)
                .over(OverClause.builder()
                        .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                        .build());
        String sql = ntile.accept(sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("NTILE(4) OVER (ORDER BY \"Employee\".\"salary\" DESC)");
    }

    @Test
    void rendersNtileWithPartitionByAndOrderBy() {
        Ntile ntile = WindowFunction.ntile(4)
                .over(OverClause.builder()
                        .partitionBy(List.of(ColumnReference.of("Employee", "department")))
                        .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                        .build());
        String sql = ntile.accept(sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "NTILE(4) OVER (PARTITION BY \"Employee\".\"department\" ORDER BY \"Employee\".\"salary\" DESC)");
    }

    @Test
    void rendersLagWithOverClauseOrderBy() {
        Lag lag = WindowFunction.lag(ColumnReference.of("Employee", "salary"), 1)
                .over(OverClause.builder()
                        .orderBy(List.of(Sorting.asc(ColumnReference.of("Employee", "hire_date"))))
                        .build());
        String sql = lag.accept(sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LAG(\"Employee\".\"salary\", 1) OVER (ORDER BY \"Employee\".\"hire_date\" ASC)");
    }

    @Test
    void rendersLagWithDefaultValueAndOverClause() {
        Lag lag = WindowFunction.lag(ColumnReference.of("Employee", "salary"), 1, Literal.of(0))
                .over(OverClause.builder()
                        .orderBy(List.of(Sorting.asc(ColumnReference.of("Employee", "hire_date"))))
                        .build());
        String sql = lag.accept(sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LAG(\"Employee\".\"salary\", 1, 0) OVER (ORDER BY \"Employee\".\"hire_date\" ASC)");
    }

    @Test
    void rendersLeadWithOverClauseOrderBy() {
        Lead lead = WindowFunction.lead(ColumnReference.of("Employee", "salary"), 1)
                .over(OverClause.builder()
                        .orderBy(List.of(Sorting.asc(ColumnReference.of("Employee", "hire_date"))))
                        .build());
        String sql = lead.accept(sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LEAD(\"Employee\".\"salary\", 1) OVER (ORDER BY \"Employee\".\"hire_date\" ASC)");
    }

    @Test
    void rendersLeadWithDefaultValueAndOverClause() {
        Lead lead = WindowFunction.lead(ColumnReference.of("Employee", "salary"), 1, Literal.of(0))
                .over(OverClause.builder()
                        .orderBy(List.of(Sorting.asc(ColumnReference.of("Employee", "hire_date"))))
                        .build());
        String sql = lead.accept(sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LEAD(\"Employee\".\"salary\", 1, 0) OVER (ORDER BY \"Employee\".\"hire_date\" ASC)");
    }

    @Test
    void rendersMultiplePartitionByColumns() {
        RowNumber rowNumber = WindowFunction.rowNumber()
                .over(OverClause.builder()
                        .partitionBy(List.of(
                                ColumnReference.of("Employee", "department"),
                                ColumnReference.of("Employee", "location")))
                        .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                        .build());
        String sql = rowNumber.accept(sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "ROW_NUMBER() OVER (PARTITION BY \"Employee\".\"department\", \"Employee\".\"location\" ORDER BY \"Employee\".\"salary\" DESC)");
    }

    @Test
    void rendersMultipleOrderByColumns() {
        RowNumber rowNumber = WindowFunction.rowNumber()
                .over(OverClause.builder()
                        .orderBy(List.of(
                                Sorting.desc(ColumnReference.of("Employee", "salary")),
                                Sorting.asc(ColumnReference.of("Employee", "hire_date"))))
                        .build());
        String sql = rowNumber.accept(sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo("ROW_NUMBER() OVER (ORDER BY \"Employee\".\"salary\" DESC, \"Employee\".\"hire_date\" ASC)");
    }
}
