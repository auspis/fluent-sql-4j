package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.ScalarExpressionProjectionPsStrategy;

public class DefaultScalarExpressionProjectionPsStrategy implements ScalarExpressionProjectionPsStrategy {
    @Override
    public PsDto handle(ScalarExpressionProjection scalarExpressionProjection, Visitor<PsDto> visitor, AstContext ctx) {
        // Visit the underlying scalar expression
        var expr = scalarExpressionProjection.getExpression();
        PsDto exprResult = expr.accept(visitor, ctx);
        String sql = exprResult.sql();
        // Handle alias if present
        String alias = scalarExpressionProjection.getAs() != null
                ? scalarExpressionProjection.getAs().getName()
                : null;
        if (alias != null && !alias.isBlank()) {
            sql += " AS \"" + alias + "\"";
        }
        return new PsDto(sql, exprResult.parameters());
    }
}
