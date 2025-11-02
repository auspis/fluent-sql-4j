package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.predicate.logical.Not;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface NotRenderStrategy extends ExpressionRenderStrategy {

    String render(Not expression, SqlRenderer sqlRenderer, AstContext ctx);
}
