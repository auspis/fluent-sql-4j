package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.DateArithmetic;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.DateArithmeticRenderStrategy;

public class MysqlDateArithmeticRenderStrategy implements DateArithmeticRenderStrategy {

    @Override
    public String render(DateArithmetic functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s(%s, INTERVAL %s %s)",
                functionCall.isAddition() ? "DATE_ADD" : "DATE_SUB",
                functionCall.dateExpression().accept(sqlRenderer, ctx),
                functionCall.interval().value().accept(sqlRenderer, ctx),
                functionCall.interval().unit().name());
    }
}
