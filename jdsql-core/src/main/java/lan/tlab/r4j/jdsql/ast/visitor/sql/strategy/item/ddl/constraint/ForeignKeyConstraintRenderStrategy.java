package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.constraint;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public interface ForeignKeyConstraintRenderStrategy extends SqlItemRenderStrategy {

    String render(ForeignKeyConstraintDefinition constraint, SqlRenderer sqlRenderer, AstContext ctx);
}
