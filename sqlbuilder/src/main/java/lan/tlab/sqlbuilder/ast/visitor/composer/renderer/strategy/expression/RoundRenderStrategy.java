package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.Round;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class RoundRenderStrategy implements ExpressionRenderStrategy {

    public String render(Round functionCall, SqlRenderer sqlRenderer) {
        return String.format(
                "ROUND(%s)",
                Stream.of(functionCall.getNumericExpression(), functionCall.getDecimalPlaces())
                        .map(e -> e.accept(sqlRenderer))
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.joining(", ")));
    }
}
