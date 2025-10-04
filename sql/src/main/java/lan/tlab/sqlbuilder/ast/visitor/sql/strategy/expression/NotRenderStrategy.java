package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.bool.logical.Not;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class NotRenderStrategy implements ExpressionRenderStrategy {

    public String render(Not expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("NOT (%s)", expression.getExpression().accept(sqlRenderer, ctx));
    }
}
