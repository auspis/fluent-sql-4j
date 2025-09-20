package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.set.NullSetExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class NullSetExpressionRenderStrategy implements ExpressionRenderStrategy {

    public String render(NullSetExpression expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return "";
    }
}
