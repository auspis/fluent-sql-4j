package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.predicate.IsNull;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface IsNullRenderStrategy extends ExpressionRenderStrategy {

    String render(IsNull expression, SqlRenderer sqlRenderer, AstContext ctx);
}
