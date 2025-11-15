package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.predicate.logical.Not;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface NotRenderStrategy extends ExpressionRenderStrategy {

    String render(Not expression, SqlRenderer sqlRenderer, AstContext ctx);
}
