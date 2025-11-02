package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.AggregateCallProjection;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlScalarSubqueryRenderStrategyTest {

    private StandardSqlScalarSubqueryRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlScalarSubqueryRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql2008();
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
