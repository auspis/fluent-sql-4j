package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.Round;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class RoundRenderStrategy implements ExpressionRenderStrategy {

    public String render(Round functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "ROUND(%s)",
                Stream.of(functionCall.getNumericExpression(), functionCall.getDecimalPlaces())
                        .map(e -> e.accept(sqlRenderer, ctx))
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.joining(", ")));
    }
}
