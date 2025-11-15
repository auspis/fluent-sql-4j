package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.predicate.logical.AndOr;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface AndOrRenderStrategy extends ExpressionRenderStrategy {

    String render(AndOr expression, SqlRenderer sqlRenderer, AstContext ctx);
}
