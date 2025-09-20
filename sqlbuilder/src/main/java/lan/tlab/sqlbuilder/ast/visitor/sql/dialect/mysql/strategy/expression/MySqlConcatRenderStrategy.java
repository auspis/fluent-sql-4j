package lan.tlab.sqlbuilder.ast.visitor.sql.dialect.mysql.strategy.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Concat;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression.ConcatRenderStrategy;

public class MySqlConcatRenderStrategy implements ConcatRenderStrategy {

    @Override
    public String render(Concat functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        String functionName = "CONCAT";
        List<ScalarExpression> expressions = new ArrayList<>();

        if (!functionCall.getSeparator().isEmpty()) {
            functionName = "CONCAT_WS";
            expressions.add(Literal.of(functionCall.getSeparator()));
        }

        expressions.addAll(functionCall.getStringExpressions());

        return String.format(
                "%s(%s)",
                functionName,
                expressions.stream().map(e -> e.accept(sqlRenderer, ctx)).collect(Collectors.joining(", ")));
    }
}
