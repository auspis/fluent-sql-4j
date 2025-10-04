package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.expression.bool.logical.AndOr;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class AndOrRenderStrategy implements ExpressionRenderStrategy {

    public String render(AndOr expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return expression.getOperands().stream()
                .map(o -> String.format("(%s)", o.accept(sqlRenderer, ctx)))
                .collect(Collectors.joining(" " + expression.getOperator().name() + " "));
    }
}
