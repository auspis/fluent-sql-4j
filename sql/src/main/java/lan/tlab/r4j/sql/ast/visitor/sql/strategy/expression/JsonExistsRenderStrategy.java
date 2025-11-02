package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonExists;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface JsonExistsRenderStrategy extends ExpressionRenderStrategy {
    String render(JsonExists functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
