package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.dll.constraint;

import lan.tlab.r4j.sql.ast.expression.item.ddl.Constraint.NotNullConstraint;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public class NotNullConstraintRenderStrategy implements SqlItemRenderStrategy {

    public String render(NotNullConstraint constraint, SqlRenderer sqlRenderer, AstContext ctx) {
        return "NOT NULL";
    }
}
