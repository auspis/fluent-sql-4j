package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class ComparisonRenderStrategy implements ExpressionRenderStrategy {

    public String render(Comparison expression, SqlRenderer sqlRenderer) {
        return String.format(
                "%s %s %s",
                expression.getLhs().accept(sqlRenderer),
                expression.getOperator().getSqlSymbol(),
                expression.getRhs().accept(sqlRenderer));
    }
}
