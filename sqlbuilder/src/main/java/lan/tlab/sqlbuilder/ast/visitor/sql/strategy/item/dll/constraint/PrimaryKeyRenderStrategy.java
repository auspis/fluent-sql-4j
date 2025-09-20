package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.dll.constraint;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.PrimaryKey;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.escape.EscapeStrategy;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class PrimaryKeyRenderStrategy implements SqlItemRenderStrategy {

    public String render(PrimaryKey item, SqlRenderer sqlRenderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = sqlRenderer.getEscapeStrategy();
        String columns =
                item.getColumns().stream().map(c -> escapeStrategy.apply(c)).collect(Collectors.joining(", "));

        return String.format("PRIMARY KEY (%s)", columns);
    }
}
