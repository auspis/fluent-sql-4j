package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.constraint;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public interface CheckConstraintRenderStrategy extends SqlItemRenderStrategy {

    String render(CheckConstraintDefinition constraint, SqlRenderer sqlRenderer, AstContext ctx);
}
