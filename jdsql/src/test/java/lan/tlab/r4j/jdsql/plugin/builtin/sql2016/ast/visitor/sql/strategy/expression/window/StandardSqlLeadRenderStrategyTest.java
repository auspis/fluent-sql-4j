package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.window.Lead;
import lan.tlab.r4j.sql.ast.common.expression.scalar.window.OverClause;
import lan.tlab.r4j.sql.ast.dql.clause.Sorting;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window.StandardSqlLeadRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlLeadRenderStrategyTest {

    private SqlRenderer sqlRenderer;
    private StandardSqlLeadRenderStrategy strategy;

    @BeforeEach
    public void setUp() {
        sqlRenderer = TestDialectRendererFactory.standardSql();
        strategy = new StandardSqlLeadRenderStrategy();
    }

    @Test
    void rendersLeadWithOverClauseOrderBy() {
        Lead lead = new Lead(
                ColumnReference.of("Employee", "salary"),
                1,
                null,
                OverClause.builder()
                        .orderBy(List.of(Sorting.asc(ColumnReference.of("Employee", "hire_date"))))
                        .build());
        String sql = strategy.render(lead, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LEAD(\"Employee\".\"salary\", 1) OVER (ORDER BY \"Employee\".\"hire_date\" ASC)");
    }

    @Test
    void rendersLeadWithDefaultValueAndOverClause() {
        Lead lead = new Lead(
                ColumnReference.of("Employee", "salary"),
                1,
                Literal.of(0),
                OverClause.builder()
                        .orderBy(List.of(Sorting.asc(ColumnReference.of("Employee", "hire_date"))))
                        .build());
        String sql = strategy.render(lead, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LEAD(\"Employee\".\"salary\", 1, 0) OVER (ORDER BY \"Employee\".\"hire_date\" ASC)");
    }
}
