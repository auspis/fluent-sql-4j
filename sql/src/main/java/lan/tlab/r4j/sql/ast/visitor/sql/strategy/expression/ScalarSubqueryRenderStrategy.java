package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class ScalarSubqueryRenderStrategy implements ExpressionRenderStrategy {

    public String render(ScalarSubquery expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("(%s)", expression.getTableExpression().accept(sqlRenderer, ctx));
    }
}
