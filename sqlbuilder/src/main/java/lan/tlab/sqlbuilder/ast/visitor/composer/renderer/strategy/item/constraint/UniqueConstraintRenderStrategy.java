package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.constraint;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.UniqueConstraint;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.escape.EscapeStrategy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.SqlItemRenderStrategy;

public class UniqueConstraintRenderStrategy implements SqlItemRenderStrategy {

    public String render(UniqueConstraint constraint, SqlRenderer sqlRenderer) {
        EscapeStrategy escapeStrategy = sqlRenderer.getEscapeStrategy();
        return String.format(
                "UNIQUE (%s)",
                constraint.getColumns().stream().map(escapeStrategy::apply).collect(Collectors.joining(", ")));
    }
}
