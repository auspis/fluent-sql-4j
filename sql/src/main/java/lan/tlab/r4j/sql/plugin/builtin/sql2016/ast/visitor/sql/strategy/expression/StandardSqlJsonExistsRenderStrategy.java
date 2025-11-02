package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.BehaviorKind;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonExists;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.JsonExistsRenderStrategy;

public class StandardSqlJsonExistsRenderStrategy implements JsonExistsRenderStrategy {

    @Override
    public String render(JsonExists functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("JSON_EXISTS(");
        sql.append(functionCall.jsonDocument().accept(sqlRenderer, ctx));
        sql.append(", ");
        sql.append(functionCall.path().accept(sqlRenderer, ctx));

        if (functionCall.onErrorBehavior() != BehaviorKind.NONE) {
            sql.append(" ").append(functionCall.onErrorBehavior().name()).append(" ON ERROR");
        }

        sql.append(")");
        return sql.toString();
    }
}
