package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonValue;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface JsonValueRenderStrategy extends ExpressionRenderStrategy {

    String render(JsonValue functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    static JsonValueRenderStrategy standardSql2016() {
        return (functionCall, sqlRenderer, ctx) -> {
            StringBuilder sql = new StringBuilder("JSON_VALUE(");
            sql.append(functionCall.jsonDocument().accept(sqlRenderer, ctx));
            sql.append(", ");
            sql.append(functionCall.path().accept(sqlRenderer, ctx));

            if (functionCall.returningType() != null) {
                sql.append(" RETURNING ").append(functionCall.returningType());
            }

            if (functionCall.onEmptyBehavior() != null) {
                sql.append(" ");
                if (functionCall.onEmptyBehavior()
                                == lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.BehaviorKind.DEFAULT
                        && functionCall.onEmptyDefault() != null) {
                    sql.append("DEFAULT ").append(functionCall.onEmptyDefault());
                } else {
                    sql.append(functionCall.onEmptyBehavior().toSql());
                }
                sql.append(" ON EMPTY");
            }

            if (functionCall.onErrorBehavior() != null) {
                sql.append(" ").append(functionCall.onErrorBehavior().toSql()).append(" ON ERROR");
            }

            sql.append(")");
            return sql.toString();
        };
    }

    static JsonValueRenderStrategy postgreSql() {
        return (functionCall, sqlRenderer, ctx) -> {
            // PostgreSQL uses jsonb_path_query_first or similar
            StringBuilder sql = new StringBuilder("JSON_VALUE(");
            sql.append(functionCall.jsonDocument().accept(sqlRenderer, ctx));
            sql.append(", ");
            sql.append(functionCall.path().accept(sqlRenderer, ctx));
            sql.append(")");
            return sql.toString();
        };
    }
}
