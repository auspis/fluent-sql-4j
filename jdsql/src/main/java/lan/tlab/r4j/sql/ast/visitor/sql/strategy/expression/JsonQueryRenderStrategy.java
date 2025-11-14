package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.JsonQuery;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface JsonQueryRenderStrategy extends ExpressionRenderStrategy {

    String render(JsonQuery functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
