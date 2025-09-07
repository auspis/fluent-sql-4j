package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll.constraint;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.DefaultConstraint;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.SqlItemRenderStrategy;

public class DefaultConstraintRenderStrategy implements SqlItemRenderStrategy {
    public String render(DefaultConstraint constraint, SqlRenderer sqlRenderer) {
        return String.format("DEFAULT %s", constraint.getValue().accept(sqlRenderer));
    }
}
