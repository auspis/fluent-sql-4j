package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.dll.constraint;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class CheckConstraintRenderStrategy implements SqlItemRenderStrategy {
    public String render(CheckConstraintDefinition constraint, SqlRenderer sqlRenderer, AstContext ctx) {
        return "CHECK (" + constraint.getExpression().accept(sqlRenderer, ctx) + ")";
    }
}
