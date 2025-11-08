package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.BehaviorKind;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.JsonQuery;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.WrapperBehavior;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.JsonQueryRenderStrategy;

public class StandardSqlJsonQueryRenderStrategy implements JsonQueryRenderStrategy {

    @Override
    public String render(JsonQuery functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
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
    }
}
