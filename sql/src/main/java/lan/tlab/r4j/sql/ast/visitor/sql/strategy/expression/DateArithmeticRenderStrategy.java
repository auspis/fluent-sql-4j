package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.DateArithmetic;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

// TODO: aaa - add tests and oracle and postgres
public interface DateArithmeticRenderStrategy extends ExpressionRenderStrategy {

    public String render(DateArithmetic functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    public static DateArithmeticRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer, ctx) -> String.format(
                "%s %s %s",
                functionCall.dateExpression().accept(sqlRenderer, ctx),
                functionCall.add() ? "+" : "-",
                functionCall.interval().accept(sqlRenderer, ctx));
    }

    public static DateArithmeticRenderStrategy sqlServer() {
        return (functionCall, sqlRenderer, ctx) -> String.format(
                "DATEADD(%s, %s%s, %s)",
                functionCall.interval().unit(),
                functionCall.add() ? "" : "-",
                functionCall.interval().value().accept(sqlRenderer, ctx),
                functionCall.dateExpression().accept(sqlRenderer, ctx));
    }

    public static DateArithmeticRenderStrategy mysql() {
        return (functionCall, sqlRenderer, ctx) -> String.format(
                "%s(%s, INTERVAL %s %s)",
                functionCall.add() ? "DATE_ADD" : "DATE_SUB",
                functionCall.dateExpression().accept(sqlRenderer, ctx),
                functionCall.interval().value().accept(sqlRenderer, ctx),
                functionCall.interval().unit().name());
    }
}
