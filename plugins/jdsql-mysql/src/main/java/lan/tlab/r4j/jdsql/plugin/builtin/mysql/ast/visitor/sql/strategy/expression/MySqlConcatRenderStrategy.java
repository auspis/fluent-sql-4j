package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.sql.strategy.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Concat;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ConcatRenderStrategy;

public class MySqlConcatRenderStrategy implements ConcatRenderStrategy {

    @Override
    public String render(Concat functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        String functionName = "CONCAT";
        List<ScalarExpression> expressions = new ArrayList<>();

        if (!functionCall.separator().isEmpty()) {
            functionName = "CONCAT_WS";
            expressions.add(Literal.of(functionCall.separator()));
        }

        expressions.addAll(functionCall.stringExpressions());

        return String.format(
                "%s(%s)",
                functionName,
                expressions.stream().map(e -> e.accept(sqlRenderer, ctx)).collect(Collectors.joining(", ")));
    }
}
