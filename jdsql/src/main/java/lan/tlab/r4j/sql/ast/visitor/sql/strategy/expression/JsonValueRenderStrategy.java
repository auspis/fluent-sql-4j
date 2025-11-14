package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.JsonValue;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface JsonValueRenderStrategy extends ExpressionRenderStrategy {

    String render(JsonValue functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
