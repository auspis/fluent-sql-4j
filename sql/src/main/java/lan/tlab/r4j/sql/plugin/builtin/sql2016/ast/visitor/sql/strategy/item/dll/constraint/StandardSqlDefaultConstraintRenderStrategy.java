package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.constraint.DefaultConstraintRenderStrategy;

public class StandardSqlDefaultConstraintRenderStrategy implements DefaultConstraintRenderStrategy {
    @Override
    public String render(DefaultConstraintDefinition constraint, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("DEFAULT %s", constraint.value().accept(sqlRenderer, ctx));
    }
}
