package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.ExtractDatePart;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class ExtractDatePartRenderStrategy implements ExpressionRenderStrategy {

    public String render(ExtractDatePart functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s(%s)",
                functionCall.getFunctionName(), functionCall.getDateExpression().accept(sqlRenderer, ctx));
    }
}
