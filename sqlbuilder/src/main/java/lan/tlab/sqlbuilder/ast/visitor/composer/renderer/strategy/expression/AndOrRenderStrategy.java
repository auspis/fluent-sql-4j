package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class AndOrRenderStrategy implements ExpressionRenderStrategy {

    public String render(AndOr expression, SqlRenderer sqlRenderer) {
        return expression.getOperands().stream()
                .map(o -> String.format("(%s)", o.accept(sqlRenderer)))
                .collect(Collectors.joining(" " + expression.getOperator().name() + " "));
    }
}
