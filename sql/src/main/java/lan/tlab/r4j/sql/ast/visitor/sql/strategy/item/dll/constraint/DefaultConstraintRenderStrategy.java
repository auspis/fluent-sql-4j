package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.dll.constraint;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class DefaultConstraintRenderStrategy implements SqlItemRenderStrategy {
    public String render(DefaultConstraintDefinition constraint, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("DEFAULT %s", constraint.getValue().accept(sqlRenderer, ctx));
    }
}
