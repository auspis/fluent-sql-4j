package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class ColumnReferenceRenderStrategy implements ExpressionRenderStrategy {

    public String render(ColumnReference expression, SqlRenderer sqlRenderer) {
        return Stream.of(expression.getTable(), expression.getColumn())
                .filter(s -> !s.isBlank())
                .map(s -> "*".equals(s) ? s : sqlRenderer.getEscapeStrategy().apply(s))
                .collect(Collectors.joining("."));
    }
}
