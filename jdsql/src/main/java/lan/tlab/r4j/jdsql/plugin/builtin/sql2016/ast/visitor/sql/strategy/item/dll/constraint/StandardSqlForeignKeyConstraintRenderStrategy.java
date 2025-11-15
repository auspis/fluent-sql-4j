package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint;

import java.util.stream.Collectors;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.escape.EscapeStrategy;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.constraint.ForeignKeyConstraintRenderStrategy;

public class StandardSqlForeignKeyConstraintRenderStrategy implements ForeignKeyConstraintRenderStrategy {
    @Override
    public String render(ForeignKeyConstraintDefinition constraint, SqlRenderer sqlRenderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = sqlRenderer.getEscapeStrategy();
        String columns =
                constraint.columns().stream().map(c -> escapeStrategy.apply(c)).collect(Collectors.joining(", "));
        return String.format(
                "FOREIGN KEY (%s) %s", columns, constraint.references().accept(sqlRenderer, ctx));
    }
}
