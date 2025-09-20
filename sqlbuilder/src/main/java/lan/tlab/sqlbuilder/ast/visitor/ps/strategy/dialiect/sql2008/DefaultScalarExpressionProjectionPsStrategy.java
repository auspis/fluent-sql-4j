package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.Expression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.ScalarExpressionProjectionPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.escape.EscapeStrategy;

public class DefaultScalarExpressionProjectionPsStrategy implements ScalarExpressionProjectionPsStrategy {
    @Override
    public PsDto handle(ScalarExpressionProjection scalarExpressionProjection, Visitor<PsDto> visitor, AstContext ctx) {
        EscapeStrategy escapeStrategy = EscapeStrategy.standard();
        if (visitor instanceof PreparedStatementVisitor psVisitor) {
            escapeStrategy = psVisitor.getEscapeStrategy();
        }

        Expression expr = scalarExpressionProjection.getExpression();
        String alias = scalarExpressionProjection.getAs().getName();

        PsDto exprResult = expr.accept(visitor, ctx);
        String sql = exprResult.sql();

        if (!alias.isBlank()) {
            sql += " AS " + escapeStrategy.apply(alias);
        }
        return new PsDto(sql, List.of());
    }
}
