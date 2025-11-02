package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class StandardSqlColumnReferenceRenderStrategy implements ExpressionRenderStrategy {

    public String render(ColumnReference expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return Stream.of(expression.table(), expression.column())
                .filter(s -> !s.isBlank())
                .map(s -> "*".equals(s) ? s : sqlRenderer.getEscapeStrategy().apply(s))
                .collect(Collectors.joining("."));
    }
}
