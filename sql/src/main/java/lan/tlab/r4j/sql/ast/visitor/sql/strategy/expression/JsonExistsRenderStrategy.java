package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonExists;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface JsonExistsRenderStrategy extends ExpressionRenderStrategy {

    String render(JsonExists functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    static JsonExistsRenderStrategy standardSql2016() {
        return (functionCall, sqlRenderer, ctx) -> {
            StringBuilder sql = new StringBuilder("JSON_EXISTS(");
            sql.append(functionCall.jsonDocument().accept(sqlRenderer, ctx));
            sql.append(", ");
            sql.append(functionCall.path().accept(sqlRenderer, ctx));

            if (functionCall.onErrorBehavior().kind()
                    != lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.BehaviorKind.NULL) {
                sql.append(" ")
                        .append(functionCall.onErrorBehavior().kind().name())
                        .append(" ON ERROR");
            }

            sql.append(")");
            return sql.toString();
        };
    }

    static JsonExistsRenderStrategy postgreSql() {
        return (functionCall, sqlRenderer, ctx) -> {
            // PostgreSQL JSON_EXISTS function
            StringBuilder sql = new StringBuilder("JSON_EXISTS(");
            sql.append(functionCall.jsonDocument().accept(sqlRenderer, ctx));
            sql.append(", ");
            sql.append(functionCall.path().accept(sqlRenderer, ctx));
            sql.append(")");
            return sql.toString();
        };
    }
}
