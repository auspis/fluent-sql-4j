package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.constraint;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public interface PrimaryKeyRenderStrategy extends SqlItemRenderStrategy {

    String render(PrimaryKeyDefinition item, SqlRenderer sqlRenderer, AstContext ctx);
}
