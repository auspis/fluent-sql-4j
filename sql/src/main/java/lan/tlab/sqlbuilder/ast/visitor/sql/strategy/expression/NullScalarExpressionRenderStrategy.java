package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class NullScalarExpressionRenderStrategy implements ExpressionRenderStrategy {

    public String render(Object expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return "";
    }
}
