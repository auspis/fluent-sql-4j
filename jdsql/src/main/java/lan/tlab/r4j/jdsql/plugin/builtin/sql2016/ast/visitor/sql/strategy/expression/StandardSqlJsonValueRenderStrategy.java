package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.BehaviorKind;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonValue;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.JsonValueRenderStrategy;

public class StandardSqlJsonValueRenderStrategy implements JsonValueRenderStrategy {

    @Override
    public String render(JsonValue functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("JSON_VALUE(");
        sql.append(functionCall.jsonDocument().accept(sqlRenderer, ctx));
        sql.append(", ");
        sql.append(functionCall.path().accept(sqlRenderer, ctx));

        if (functionCall.returningType() != null) {
            sql.append(" RETURNING ").append(functionCall.returningType());
        }

        if (functionCall.onEmptyBehavior().kind() != BehaviorKind.NONE) {
            sql.append(" ");
            if (functionCall.onEmptyBehavior().kind() == BehaviorKind.DEFAULT
                    && functionCall.onEmptyBehavior().defaultValue() != null) {
                sql.append("DEFAULT ").append(functionCall.onEmptyBehavior().defaultValue());
            } else {
                sql.append(functionCall.onEmptyBehavior().kind().name());
            }
            sql.append(" ON EMPTY");
        }

        if (functionCall.onErrorBehavior() != BehaviorKind.NONE) {
            sql.append(" ").append(functionCall.onErrorBehavior().name()).append(" ON ERROR");
        }

        sql.append(")");
        return sql.toString();
    }
}
