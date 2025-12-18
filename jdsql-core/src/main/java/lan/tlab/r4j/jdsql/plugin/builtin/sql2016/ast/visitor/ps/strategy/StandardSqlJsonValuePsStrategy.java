package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.function.json.BehaviorKind;
import lan.tlab.r4j.jdsql.ast.core.expression.function.json.JsonValue;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.JsonValuePsStrategy;

public class StandardSqlJsonValuePsStrategy implements JsonValuePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            JsonValue jsonValue, AstToPreparedStatementSpecVisitor renderer, AstContext ctx) {
        var documentResult = jsonValue.jsonDocument().accept(renderer, ctx);
        var pathResult = jsonValue.path().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(documentResult.parameters());
        parameters.addAll(pathResult.parameters());

        StringBuilder sql = new StringBuilder("JSON_VALUE(");
        sql.append(documentResult.sql());
        sql.append(", ");
        sql.append(pathResult.sql());

        if (jsonValue.returningType() != null) {
            sql.append(" RETURNING ").append(jsonValue.returningType());
        }

        if (jsonValue.onEmptyBehavior().kind() != BehaviorKind.NONE) {
            sql.append(" ");
            if (jsonValue.onEmptyBehavior().kind() == BehaviorKind.DEFAULT
                    && jsonValue.onEmptyBehavior().defaultValue() != null) {
                sql.append("DEFAULT ").append(jsonValue.onEmptyBehavior().defaultValue());
            } else {
                sql.append(jsonValue.onEmptyBehavior().kind().name());
            }
            sql.append(" ON EMPTY");
        }

        if (jsonValue.onErrorBehavior() != BehaviorKind.NONE) {
            sql.append(" ").append(jsonValue.onErrorBehavior().name()).append(" ON ERROR");
        }

        sql.append(")");
        return new PreparedStatementSpec(sql.toString(), parameters);
    }
}
