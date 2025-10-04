package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.dll.constraint;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.Constraint.ForeignKeyConstraint;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape.EscapeStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class ForeignKeyConstraintRenderStrategy implements SqlItemRenderStrategy {

    public String render(ForeignKeyConstraint constraint, SqlRenderer sqlRenderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = sqlRenderer.getEscapeStrategy();
        String columns = constraint.getColumns().stream()
                .map(c -> escapeStrategy.apply(c))
                .collect(Collectors.joining(", "));
        return String.format(
                "FOREIGN KEY (%s) %s", columns, constraint.getReferences().accept(sqlRenderer, ctx));
    }
}
