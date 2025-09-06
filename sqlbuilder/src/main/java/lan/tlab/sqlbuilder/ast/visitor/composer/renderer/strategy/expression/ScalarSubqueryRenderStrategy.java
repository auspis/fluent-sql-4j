package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarSubquery;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class ScalarSubqueryRenderStrategy implements ExpressionRenderStrategy {

    public String render(ScalarSubquery expression, SqlRenderer sqlRenderer) {
        return String.format("(%s)", expression.getTableExpression().accept(sqlRenderer));
    }
}
