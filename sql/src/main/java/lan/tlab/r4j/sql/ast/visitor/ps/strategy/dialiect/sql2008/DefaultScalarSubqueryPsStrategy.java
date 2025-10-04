package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ScalarSubqueryPsStrategy;

public class DefaultScalarSubqueryPsStrategy implements ScalarSubqueryPsStrategy {

    @Override
    public PsDto handle(ScalarSubquery subquery, PreparedStatementVisitor visitor, AstContext ctx) {
        // Scalar subqueries contain table expressions that may have parameters
        // Use hybrid approach: delegate to visitor for parameter handling
        PsDto innerResult = subquery.getTableExpression().accept(visitor, ctx);

        // Wrap in parentheses as required for scalar subqueries
        String sql = "(" + innerResult.sql() + ")";
        List<Object> parameters = new ArrayList<>(innerResult.parameters());

        return new PsDto(sql, parameters);
    }
}
