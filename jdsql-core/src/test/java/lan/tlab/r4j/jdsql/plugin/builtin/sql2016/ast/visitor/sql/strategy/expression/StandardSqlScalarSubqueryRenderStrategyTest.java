package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.jdsql.ast.dql.clause.From;
import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.dql.projection.AggregateCallProjection;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlScalarSubqueryRenderStrategyTest {

    private StandardSqlScalarSubqueryRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlScalarSubqueryRenderStrategy();
        sqlRenderer = StandardSqlRendererFactory.standardSql();
    }

    @Test
    void ok() {
        ScalarSubquery subquery = ScalarSubquery.builder()
                .tableExpression(SelectStatement.builder()
                        .select(Select.builder()
                                .projection(new AggregateCallProjection(
                                        AggregateCall.max(ColumnReference.of("Risk", "value"))))
                                .build())
                        .from(From.fromTable("Risk"))
                        .build())
                .build();
        String sql = strategy.render(subquery, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("(SELECT MAX(\"Risk\".\"value\") FROM \"Risk\")");
    }
}
