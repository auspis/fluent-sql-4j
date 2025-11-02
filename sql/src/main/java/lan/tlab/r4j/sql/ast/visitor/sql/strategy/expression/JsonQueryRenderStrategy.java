package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.BehaviorKind;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonQuery;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.WrapperBehavior;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface JsonQueryRenderStrategy extends ExpressionRenderStrategy {

    String render(JsonQuery functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    static JsonQueryRenderStrategy standardSql2016() {
        return (functionCall, sqlRenderer, ctx) -> {
            StringBuilder sql = new StringBuilder("JSON_QUERY(");
            sql.append(functionCall.jsonDocument().accept(sqlRenderer, ctx));
            sql.append(", ");
            sql.append(functionCall.path().accept(sqlRenderer, ctx));

            if (functionCall.returningType() != null) {
                sql.append(" RETURNING ").append(functionCall.returningType());
            }

            if (functionCall.wrapperBehavior() != WrapperBehavior.NONE) {
                sql.append(" ").append(functionCall.wrapperBehavior().name().replace("_", " "));
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
        };
    }

    static JsonQueryRenderStrategy postgreSql() {
        return (functionCall, sqlRenderer, ctx) -> {
            // PostgreSQL JSON_QUERY function
            StringBuilder sql = new StringBuilder("JSON_QUERY(");
            sql.append(functionCall.jsonDocument().accept(sqlRenderer, ctx));
            sql.append(", ");
            sql.append(functionCall.path().accept(sqlRenderer, ctx));
            sql.append(")");
            return sql.toString();
        };
    }
}
