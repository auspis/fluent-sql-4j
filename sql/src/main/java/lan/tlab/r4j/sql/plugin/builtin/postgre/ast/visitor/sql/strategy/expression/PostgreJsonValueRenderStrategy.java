package lan.tlab.r4j.sql.plugin.builtin.postgre.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonValue;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.JsonValueRenderStrategy;

public class PostgreJsonValueRenderStrategy implements JsonValueRenderStrategy {

    @Override
    public String render(JsonValue functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("JSON_VALUE(");
        sql.append(functionCall.jsonDocument().accept(sqlRenderer, ctx));
        sql.append(", ");
        sql.append(functionCall.path().accept(sqlRenderer, ctx));
        sql.append(")");
        return sql.toString();
    }
}
