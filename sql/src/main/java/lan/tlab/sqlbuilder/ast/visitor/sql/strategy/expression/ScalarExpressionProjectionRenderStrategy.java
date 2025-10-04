package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class ScalarExpressionProjectionRenderStrategy implements ExpressionRenderStrategy {

    public String render(ScalarExpressionProjection projection, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                        "%s %s",
                        projection.getExpression().accept(sqlRenderer, ctx),
                        projection.getAs().accept(sqlRenderer, ctx))
                .trim();
    }
}
