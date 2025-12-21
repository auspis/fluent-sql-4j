package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Cast;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CastPsStrategy;

public class StandardSqlCastPsStrategy implements CastPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            Cast cast, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        // Visit the inner expression to get its SQL and parameters
        PreparedStatementSpec expressionDto = cast.expression().accept(astToPsSpecVisitor, ctx);

        // Build the CAST SQL syntax
        String sql = "CAST(" + expressionDto.sql() + " AS " + cast.dataType() + ")";

        // Return the parameters from the inner expression
        return new PreparedStatementSpec(sql, expressionDto.parameters());
    }
}
