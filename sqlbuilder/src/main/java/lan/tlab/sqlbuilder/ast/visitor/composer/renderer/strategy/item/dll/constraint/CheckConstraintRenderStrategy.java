package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll.constraint;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.CheckConstraint;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.SqlItemRenderStrategy;

public class CheckConstraintRenderStrategy implements SqlItemRenderStrategy {
    public String render(CheckConstraint constraint, SqlRenderer sqlRenderer) {
        return "CHECK (" + constraint.getExpression().accept(sqlRenderer) + ")";
    }
}
