package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.DateArithmetic;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

// TODO: aaa - add tests and oracle and postgres
public interface DateArithmeticRenderStrategy extends ExpressionRenderStrategy {

    public String render(DateArithmetic functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    public static DateArithmeticRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer, ctx) -> String.format(
                "%s %s %s",
                functionCall.getDateExpression().accept(sqlRenderer, ctx),
                functionCall.isAdd() ? "+" : "-",
                functionCall.getInterval().accept(sqlRenderer, ctx));
    }

    public static DateArithmeticRenderStrategy sqlServer() {
        return (functionCall, sqlRenderer, ctx) -> String.format(
                "DATEADD(%s, %s%s, %s)",
                functionCall.getInterval().getUnit(),
                functionCall.isAdd() ? "" : "-",
                functionCall.getInterval().getValue().accept(sqlRenderer, ctx),
                functionCall.getDateExpression().accept(sqlRenderer, ctx));
    }

    public static DateArithmeticRenderStrategy mysql() {
        return (functionCall, sqlRenderer, ctx) -> String.format(
                "%s(%s, %s)",
                functionCall.isAdd() ? "DATE_ADD" : "DATE_SUB",
                functionCall.getDateExpression().accept(sqlRenderer, ctx),
                functionCall.getInterval().accept(sqlRenderer, ctx));
    }
}
