package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.dll.constraint;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.CheckConstraint;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class CheckConstraintRenderStrategy implements SqlItemRenderStrategy {
    public String render(CheckConstraint constraint, SqlRenderer sqlRenderer, AstContext ctx) {
        return "CHECK (" + constraint.getExpression().accept(sqlRenderer, ctx) + ")";
    }
}
