package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.predicate.logical.AndOr;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface AndOrRenderStrategy extends ExpressionRenderStrategy {

    String render(AndOr expression, SqlRenderer sqlRenderer, AstContext ctx);
}
