package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.bool.logical.Not;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class NotRenderStrategy implements ExpressionRenderStrategy {

    public String render(Not expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("NOT (%s)", expression.getExpression().accept(sqlRenderer, ctx));
    }
}
