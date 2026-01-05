package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Cast;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CastPsStrategy;

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
