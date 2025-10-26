package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Substring;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class SubstringRenderStrategy implements ExpressionRenderStrategy {

    public String render(Substring functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        Stream<ScalarExpression> stream =
                Stream.of(functionCall.expression(), functionCall.startPosition(), functionCall.length());

        return String.format(
                "SUBSTRING(%s)",
                stream.map(e -> e.accept(sqlRenderer, ctx))
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.joining(", ")));
    }
}
