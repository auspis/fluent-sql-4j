package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarSubquery;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ScalarSubqueryPsStrategy;

public class StandardSqlScalarSubqueryPsStrategy implements ScalarSubqueryPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            ScalarSubquery subquery, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        // Scalar subqueries contain table expressions that may have parameters
        // Use hybrid approach: delegate to visitor for parameter handling
        PreparedStatementSpec innerResult = subquery.tableExpression().accept(astToPsSpecVisitor, ctx);

        // Wrap in parentheses as required for scalar subqueries
        String sql = "(" + innerResult.sql() + ")";
        List<Object> parameters = new ArrayList<>(innerResult.parameters());

        return new PreparedStatementSpec(sql, parameters);
    }
}
