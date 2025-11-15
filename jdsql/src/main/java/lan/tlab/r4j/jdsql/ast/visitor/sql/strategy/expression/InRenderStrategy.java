package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.predicate.In;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface InRenderStrategy extends ExpressionRenderStrategy {

    String render(In expression, SqlRenderer sqlRenderer, AstContext ctx);
}
