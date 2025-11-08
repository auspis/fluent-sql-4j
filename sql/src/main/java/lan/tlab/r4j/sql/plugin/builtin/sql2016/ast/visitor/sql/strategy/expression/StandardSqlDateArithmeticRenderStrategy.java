package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.DateArithmetic;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.DateArithmeticRenderStrategy;

public class StandardSqlDateArithmeticRenderStrategy implements DateArithmeticRenderStrategy {

    @Override
    public String render(DateArithmetic functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s %s %s",
                functionCall.dateExpression().accept(sqlRenderer, ctx),
                functionCall.isAddition() ? "+" : "-",
                functionCall.interval().accept(sqlRenderer, ctx));
    }
}
