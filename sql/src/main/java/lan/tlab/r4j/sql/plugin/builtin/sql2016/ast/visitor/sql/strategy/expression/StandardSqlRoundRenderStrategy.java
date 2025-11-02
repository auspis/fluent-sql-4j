package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.Round;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.RoundRenderStrategy;

public class StandardSqlRoundRenderStrategy implements RoundRenderStrategy {

    @Override
    public String render(Round functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "ROUND(%s)",
                Stream.of(functionCall.numericExpression(), functionCall.decimalPlaces())
                        .map(e -> e.accept(sqlRenderer, ctx))
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.joining(", ")));
    }
}
