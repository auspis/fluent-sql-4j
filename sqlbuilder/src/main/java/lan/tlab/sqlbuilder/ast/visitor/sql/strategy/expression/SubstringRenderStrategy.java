package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Substring;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class SubstringRenderStrategy implements ExpressionRenderStrategy {

    public String render(Substring functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        Stream<ScalarExpression> stream =
                Stream.of(functionCall.getExpression(), functionCall.getStartPosition(), functionCall.getLength());

        return String.format(
                "SUBSTRING(%s)",
                stream.map(e -> e.accept(sqlRenderer, ctx))
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.joining(", ")));
    }
}
