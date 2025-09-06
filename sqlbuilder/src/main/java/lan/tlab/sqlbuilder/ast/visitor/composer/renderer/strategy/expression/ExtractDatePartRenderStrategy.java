package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.ExtractDatePart;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class ExtractDatePartRenderStrategy implements ExpressionRenderStrategy {

    public String render(ExtractDatePart functionCall, SqlRenderer sqlRenderer) {
        return String.format(
                "%s(%s)",
                functionCall.getFunctionName(), functionCall.getDateExpression().accept(sqlRenderer));
    }
}
