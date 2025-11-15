package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.OverClause;
import lan.tlab.r4j.jdsql.ast.dql.clause.Sorting;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlOverClauseRenderStrategyTest {

    private SqlRenderer sqlRenderer;
    private StandardSqlOverClauseRenderStrategy strategy;

    @BeforeEach
    public void setUp() {
        sqlRenderer = TestDialectRendererFactory.standardSql();
        strategy = new StandardSqlOverClauseRenderStrategy();
    }

    @Test
    void rendersOverClauseWithPartitionByAndOrderBy() {
        OverClause overClause = OverClause.builder()
                .partitionBy(List.of(ColumnReference.of("Employee", "department")))
                .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                .build();

        String sql = strategy.render(overClause, sqlRenderer, new AstContext());

        assertThat(sql)
                .isEqualTo("OVER (PARTITION BY \"Employee\".\"department\" ORDER BY \"Employee\".\"salary\" DESC)");
    }

    @Test
    void rendersOverClauseWithOnlyPartitionBy() {
        OverClause overClause = OverClause.builder()
                .partitionBy(List.of(ColumnReference.of("Employee", "department")))
                .build();

        String sql = strategy.render(overClause, sqlRenderer, new AstContext());

        assertThat(sql).isEqualTo("OVER (PARTITION BY \"Employee\".\"department\")");
    }

    @Test
    void rendersOverClauseWithOnlyOrderBy() {
        OverClause overClause = OverClause.builder()
                .orderBy(List.of(Sorting.asc(ColumnReference.of("Employee", "hire_date"))))
                .build();

        String sql = strategy.render(overClause, sqlRenderer, new AstContext());

        assertThat(sql).isEqualTo("OVER (ORDER BY \"Employee\".\"hire_date\" ASC)");
    }

    @Test
    void rendersOverClauseWithEmptyPartitionByAndOrderBy() {
        OverClause overClause =
                OverClause.builder().partitionBy(List.of()).orderBy(List.of()).build();

        String sql = strategy.render(overClause, sqlRenderer, new AstContext());

        assertThat(sql).isEqualTo("OVER ()");
    }

    @Test
    void rendersOverClauseWithNullPartitionByAndOrderBy() {
        List<ScalarExpression> partitionBy = null;
        List<Sorting> orderBy = null;
        OverClause overClause =
                OverClause.builder().partitionBy(partitionBy).orderBy(orderBy).build();

        String sql = strategy.render(overClause, sqlRenderer, new AstContext());

        assertThat(sql).isEqualTo("OVER ()");
    }

    @Test
    void rendersOverClauseWithMultiplePartitionByColumns() {
        OverClause overClause = OverClause.builder()
                .partitionBy(List.of(
                        ColumnReference.of("Employee", "department"), ColumnReference.of("Employee", "location")))
                .build();

        String sql = strategy.render(overClause, sqlRenderer, new AstContext());

        assertThat(sql).isEqualTo("OVER (PARTITION BY \"Employee\".\"department\", \"Employee\".\"location\")");
    }

    @Test
    void rendersOverClauseWithMultipleOrderByColumns() {
        OverClause overClause = OverClause.builder()
                .orderBy(List.of(
                        Sorting.desc(ColumnReference.of("Employee", "salary")),
                        Sorting.asc(ColumnReference.of("Employee", "hire_date"))))
                .build();

        String sql = strategy.render(overClause, sqlRenderer, new AstContext());

        assertThat(sql).isEqualTo("OVER (ORDER BY \"Employee\".\"salary\" DESC, \"Employee\".\"hire_date\" ASC)");
    }

    @Test
    void rendersOverClauseWithEmptyPartitionByAndPresentOrderBy() {
        OverClause overClause = OverClause.builder()
                .partitionBy(List.of()) // Empty list, not null
                .orderBy(List.of(Sorting.asc(ColumnReference.of("Employee", "hire_date"))))
                .build();

        String sql = strategy.render(overClause, sqlRenderer, new AstContext());

        assertThat(sql).isEqualTo("OVER (ORDER BY \"Employee\".\"hire_date\" ASC)");
    }
}
