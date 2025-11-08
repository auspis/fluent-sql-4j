package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ColumnReferenceRenderStrategy;

public class StandardSqlColumnReferenceRenderStrategy implements ColumnReferenceRenderStrategy {

    @Override
    public String render(ColumnReference expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return Stream.of(expression.table(), expression.column())
                .filter(s -> !s.isBlank())
                .map(s -> "*".equals(s) ? s : sqlRenderer.getEscapeStrategy().apply(s))
                .collect(Collectors.joining("."));
    }
}
