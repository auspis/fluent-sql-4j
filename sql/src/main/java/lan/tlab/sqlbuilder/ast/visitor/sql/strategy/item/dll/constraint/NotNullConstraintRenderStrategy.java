package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.dll.constraint;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.NotNullConstraint;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class NotNullConstraintRenderStrategy implements SqlItemRenderStrategy {

    public String render(NotNullConstraint constraint, SqlRenderer sqlRenderer, AstContext ctx) {
        return "NOT NULL";
    }
}
