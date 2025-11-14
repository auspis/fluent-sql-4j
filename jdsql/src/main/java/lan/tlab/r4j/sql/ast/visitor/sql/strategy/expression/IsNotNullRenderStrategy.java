package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.predicate.IsNotNull;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface IsNotNullRenderStrategy extends ExpressionRenderStrategy {

    String render(IsNotNull expression, SqlRenderer sqlRenderer, AstContext ctx);
}
