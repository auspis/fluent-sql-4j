package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.clause.orderby.Sorting;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.OverClause;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.RowNumber;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RowNumberRenderStrategyTest {

    private SqlRenderer sqlRenderer;
    private RowNumberRenderStrategy strategy;

    @BeforeEach
    public void setUp() {
        sqlRenderer = TestDialectRendererFactory.standardSql();
        strategy = new RowNumberRenderStrategy();
    }

    @Test
    void rendersRowNumberWithOverClauseOrderBy() {
        RowNumber rowNumber = new RowNumber(OverClause.builder()
                .orderBy(List.of(Sorting.asc(ColumnReference.of("Employee", "salary"))))
                .build());
        String sql = strategy.render(rowNumber, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("ROW_NUMBER() OVER (ORDER BY \"Employee\".\"salary\" ASC)");
    }

    @Test
    void rendersRowNumberWithOverClausePartitionByAndOrderBy() {
        RowNumber rowNumber = new RowNumber(OverClause.builder()
                .partitionBy(List.of(ColumnReference.of("Employee", "department")))
                .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                .build());
        String sql = strategy.render(rowNumber, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "ROW_NUMBER() OVER (PARTITION BY \"Employee\".\"department\" ORDER BY \"Employee\".\"salary\" DESC)");
    }

    @Test
    void rendersMultiplePartitionByColumns() {
        RowNumber rowNumber = new RowNumber(OverClause.builder()
                .partitionBy(List.of(
                        ColumnReference.of("Employee", "department"), ColumnReference.of("Employee", "location")))
                .orderBy(List.of(Sorting.desc(ColumnReference.of("Employee", "salary"))))
                .build());
        String sql = strategy.render(rowNumber, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "ROW_NUMBER() OVER (PARTITION BY \"Employee\".\"department\", \"Employee\".\"location\" ORDER BY \"Employee\".\"salary\" DESC)");
    }

    @Test
    void rendersMultipleOrderByColumns() {
        RowNumber rowNumber = new RowNumber(OverClause.builder()
                .orderBy(List.of(
                        Sorting.desc(ColumnReference.of("Employee", "salary")),
                        Sorting.asc(ColumnReference.of("Employee", "hire_date"))))
                .build());
        String sql = strategy.render(rowNumber, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo("ROW_NUMBER() OVER (ORDER BY \"Employee\".\"salary\" DESC, \"Employee\".\"hire_date\" ASC)");
    }
}
