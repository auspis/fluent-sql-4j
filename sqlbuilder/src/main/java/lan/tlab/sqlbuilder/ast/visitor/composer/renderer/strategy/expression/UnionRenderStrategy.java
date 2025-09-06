package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.set.UnionExpression;
import lan.tlab.sqlbuilder.ast.expression.set.UnionExpression.UnionType;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class UnionRenderStrategy implements ExpressionRenderStrategy {

    public String render(UnionExpression expression, SqlRenderer sqlRenderer) {
        return String.format(
                "((%s) %s (%s))",
                expression.getLeft().accept(sqlRenderer),
                (expression.getType() == UnionType.UNION_DISTINCT ? "UNION" : "UNION ALL"),
                expression.getRight().accept(sqlRenderer));
    }
}
