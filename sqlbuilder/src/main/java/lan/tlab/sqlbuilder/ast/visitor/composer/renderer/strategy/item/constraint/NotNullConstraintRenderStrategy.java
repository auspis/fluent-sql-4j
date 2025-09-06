package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.constraint;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.NotNullConstraint;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.SqlItemRenderStrategy;

public class NotNullConstraintRenderStrategy implements SqlItemRenderStrategy {

    public String render(NotNullConstraint constraint, SqlRenderer sqlRenderer) {
        return "NOT NULL";
    }
}
