package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregateCallProjection;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarSubquery;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScalarSubqueryRenderStrategyTest {

    private ScalarSubqueryRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new ScalarSubqueryRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
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
