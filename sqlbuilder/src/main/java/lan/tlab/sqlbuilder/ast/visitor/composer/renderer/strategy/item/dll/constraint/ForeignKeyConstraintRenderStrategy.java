package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll.constraint;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.ForeignKeyConstraint;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.escape.EscapeStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.SqlItemRenderStrategy;

public class ForeignKeyConstraintRenderStrategy implements SqlItemRenderStrategy {

    public String render(ForeignKeyConstraint constraint, SqlRenderer sqlRenderer) {
        EscapeStrategy escapeStrategy = sqlRenderer.getEscapeStrategy();
        String columns = constraint.getColumns().stream()
                .map(c -> escapeStrategy.apply(c))
                .collect(Collectors.joining(", "));
        return String.format(
                "FOREIGN KEY (%s) %s", columns, constraint.getReferences().accept(sqlRenderer));
    }
}
