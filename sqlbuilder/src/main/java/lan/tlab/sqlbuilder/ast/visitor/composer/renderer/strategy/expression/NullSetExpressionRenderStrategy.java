package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.set.NullSetExpression;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class NullSetExpressionRenderStrategy implements ExpressionRenderStrategy {

    public String render(NullSetExpression expression, SqlRenderer sqlRenderer) {
        return "";
    }
}
