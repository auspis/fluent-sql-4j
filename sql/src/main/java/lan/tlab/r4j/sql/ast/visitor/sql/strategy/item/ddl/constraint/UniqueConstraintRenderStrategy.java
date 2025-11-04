package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.constraint;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public interface UniqueConstraintRenderStrategy extends SqlItemRenderStrategy {

    String render(UniqueConstraintDefinition constraint, SqlRenderer sqlRenderer, AstContext ctx);
}
