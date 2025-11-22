package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number.UnaryNumeric;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.UnaryNumericRenderStrategy;

public class StandardSqlUnaryNumericRenderStrategy implements UnaryNumericRenderStrategy {

    @Override
    public String render(UnaryNumeric functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s(%s)",
                functionCall.functionName(), functionCall.numericExpression().accept(sqlRenderer, ctx));
    }
}
