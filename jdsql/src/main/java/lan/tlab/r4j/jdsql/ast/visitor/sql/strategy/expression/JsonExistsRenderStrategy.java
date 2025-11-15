package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonExists;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface JsonExistsRenderStrategy extends ExpressionRenderStrategy {
    String render(JsonExists functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
