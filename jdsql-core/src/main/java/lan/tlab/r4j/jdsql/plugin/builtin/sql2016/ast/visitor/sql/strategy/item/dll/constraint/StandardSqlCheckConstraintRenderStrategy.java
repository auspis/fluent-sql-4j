package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.constraint.CheckConstraintRenderStrategy;

public class StandardSqlCheckConstraintRenderStrategy implements CheckConstraintRenderStrategy {
    @Override
    public String render(CheckConstraintDefinition constraint, SqlRenderer sqlRenderer, AstContext ctx) {
        return "CHECK (" + constraint.expression().accept(sqlRenderer, ctx) + ")";
    }
}
