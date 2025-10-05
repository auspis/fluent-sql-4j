package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.constraint;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class NotNullConstraintRenderStrategy implements SqlItemRenderStrategy {

    public String render(NotNullConstraintDefinition constraint, SqlRenderer sqlRenderer, AstContext ctx) {
        return "NOT NULL";
    }
}
