package lan.tlab.r4j.jdsql.plugin.builtin.postgre.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonQuery;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.JsonQueryRenderStrategy;

public class PostgreJsonQueryRenderStrategy implements JsonQueryRenderStrategy {

    @Override
    public String render(JsonQuery functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("JSON_QUERY(");
        sql.append(functionCall.jsonDocument().accept(sqlRenderer, ctx));
        sql.append(", ");
        sql.append(functionCall.path().accept(sqlRenderer, ctx));
        sql.append(")");
        return sql.toString();
    }
}
