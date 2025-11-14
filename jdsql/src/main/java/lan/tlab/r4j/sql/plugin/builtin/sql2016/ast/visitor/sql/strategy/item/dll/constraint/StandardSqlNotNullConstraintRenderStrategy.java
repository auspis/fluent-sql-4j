package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint;

import lan.tlab.r4j.sql.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.constraint.NotNullConstraintRenderStrategy;

public class StandardSqlNotNullConstraintRenderStrategy implements NotNullConstraintRenderStrategy {
    @Override
    public String render(NotNullConstraintDefinition constraint, SqlRenderer sqlRenderer, AstContext ctx) {
        return "NOT NULL";
    }
}
