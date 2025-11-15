package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.CustomFunctionCallRenderStrategy;

/**
 * Standard SQL rendering strategy for custom function calls.
 * <p>
 * This strategy renders custom functions with their arguments, but does not
 * handle function options. For dialect-specific rendering with options support,
 * use a dialect-specific strategy (e.g., {@link lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.sql.strategy.expression.MysqlCustomFunctionCallRenderStrategy}).
 */
public class StandardSqlCustomFunctionCallRenderStrategy implements CustomFunctionCallRenderStrategy {

    @Override
    public String render(CustomFunctionCall functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        String args = functionCall.arguments().stream()
                .map(arg -> arg.accept(sqlRenderer, ctx))
                .collect(Collectors.joining(", "));

        return functionCall.functionName() + "(" + args + ")";
    }
}
