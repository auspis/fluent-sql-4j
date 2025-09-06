package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class NullScalarExpressionRenderStrategy implements ExpressionRenderStrategy {

    public String render(Object expression, SqlRenderer sqlRenderer) {
        return "";
    }
}
