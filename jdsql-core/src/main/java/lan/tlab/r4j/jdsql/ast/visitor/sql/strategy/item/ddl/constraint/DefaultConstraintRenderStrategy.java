package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.ddl.constraint;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.item.SqlItemRenderStrategy;

public interface DefaultConstraintRenderStrategy extends SqlItemRenderStrategy {

    String render(DefaultConstraintDefinition constraint, SqlRenderer sqlRenderer, AstContext ctx);
}
