package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.dll.constraint;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.UniqueConstraint;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.escape.EscapeStrategy;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class UniqueConstraintRenderStrategy implements SqlItemRenderStrategy {

    public String render(UniqueConstraint constraint, SqlRenderer sqlRenderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = sqlRenderer.getEscapeStrategy();
        return String.format(
                "UNIQUE (%s)",
                constraint.getColumns().stream().map(escapeStrategy::apply).collect(Collectors.joining(", ")));
    }
}
