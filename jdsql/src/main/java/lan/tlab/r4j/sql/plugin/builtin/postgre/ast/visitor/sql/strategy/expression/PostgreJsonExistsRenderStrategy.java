package lan.tlab.r4j.sql.plugin.builtin.postgre.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.JsonExists;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.JsonExistsRenderStrategy;

public class PostgreJsonExistsRenderStrategy implements JsonExistsRenderStrategy {

    @Override
    public String render(JsonExists functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("JSON_EXISTS(");
        sql.append(functionCall.jsonDocument().accept(sqlRenderer, ctx));
        sql.append(", ");
        sql.append(functionCall.path().accept(sqlRenderer, ctx));
        sql.append(")");
        return sql.toString();
    }
}
