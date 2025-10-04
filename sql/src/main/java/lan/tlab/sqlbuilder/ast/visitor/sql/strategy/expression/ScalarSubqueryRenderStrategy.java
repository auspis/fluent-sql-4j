package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarSubquery;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class ScalarSubqueryRenderStrategy implements ExpressionRenderStrategy {

    public String render(ScalarSubquery expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("(%s)", expression.getTableExpression().accept(sqlRenderer, ctx));
    }
}
