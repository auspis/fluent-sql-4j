package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.constraint;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape.EscapeStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class UniqueConstraintRenderStrategy implements SqlItemRenderStrategy {

    public String render(UniqueConstraintDefinition constraint, SqlRenderer sqlRenderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = sqlRenderer.getEscapeStrategy();
        String columns =
                constraint.columns().stream().map(c -> escapeStrategy.apply(c)).collect(Collectors.joining(", "));
        return String.format("UNIQUE (%s)", columns);
    }
}
