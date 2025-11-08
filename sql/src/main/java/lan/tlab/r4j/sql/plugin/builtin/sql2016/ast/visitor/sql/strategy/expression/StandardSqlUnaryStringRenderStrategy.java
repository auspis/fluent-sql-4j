package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.UnaryString;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.UnaryStringRenderStrategy;

public class StandardSqlUnaryStringRenderStrategy implements UnaryStringRenderStrategy {

    @Override
    public String render(UnaryString functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s(%s)", functionCall.functionName(), functionCall.expression().accept(sqlRenderer, ctx));
    }
}
