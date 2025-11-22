package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.constraint;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public interface NotNullConstraintRenderStrategy extends SqlItemRenderStrategy {

    String render(NotNullConstraintDefinition constraint, SqlRenderer sqlRenderer, AstContext ctx);
}
