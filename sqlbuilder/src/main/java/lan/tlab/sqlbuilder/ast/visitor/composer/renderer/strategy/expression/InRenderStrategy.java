package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.bool.In;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class InRenderStrategy implements ExpressionRenderStrategy {

    public String render(In expression, SqlRenderer sqlRenderer) {
        return String.format(
                "%s IN(%s)",
                expression.getExpression().accept(sqlRenderer),
                expression.getValues().stream().map(e -> e.accept(sqlRenderer)).collect(Collectors.joining(", ")));
    }
}
