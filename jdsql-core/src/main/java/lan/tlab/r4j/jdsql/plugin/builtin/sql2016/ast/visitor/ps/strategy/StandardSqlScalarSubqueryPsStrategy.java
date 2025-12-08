package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ScalarSubqueryPsStrategy;

public class StandardSqlScalarSubqueryPsStrategy implements ScalarSubqueryPsStrategy {

    @Override
    public PreparedStatementSpec handle(ScalarSubquery subquery, PreparedStatementRenderer renderer, AstContext ctx) {
        // Scalar subqueries contain table expressions that may have parameters
        // Use hybrid approach: delegate to visitor for parameter handling
        PreparedStatementSpec innerResult = subquery.tableExpression().accept(renderer, ctx);

        // Wrap in parentheses as required for scalar subqueries
        String sql = "(" + innerResult.sql() + ")";
        List<Object> parameters = new ArrayList<>(innerResult.parameters());

        return new PreparedStatementSpec(sql, parameters);
    }
}
