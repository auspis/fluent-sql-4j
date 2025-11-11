package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ScalarSubqueryPsStrategy;

public class StandardSqlScalarSubqueryPsStrategy implements ScalarSubqueryPsStrategy {

    @Override
    public PsDto handle(ScalarSubquery subquery, PreparedStatementRenderer renderer, AstContext ctx) {
        // Scalar subqueries contain table expressions that may have parameters
        // Use hybrid approach: delegate to visitor for parameter handling
        PsDto innerResult = subquery.tableExpression().accept(renderer, ctx);

        // Wrap in parentheses as required for scalar subqueries
        String sql = "(" + innerResult.sql() + ")";
        List<Object> parameters = new ArrayList<>(innerResult.parameters());

        return new PsDto(sql, parameters);
    }
}
