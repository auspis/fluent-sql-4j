package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.dll.constraint;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.DefaultConstraint;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class DefaultConstraintRenderStrategy implements SqlItemRenderStrategy {
    public String render(DefaultConstraint constraint, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("DEFAULT %s", constraint.getValue().accept(sqlRenderer, ctx));
    }
}
