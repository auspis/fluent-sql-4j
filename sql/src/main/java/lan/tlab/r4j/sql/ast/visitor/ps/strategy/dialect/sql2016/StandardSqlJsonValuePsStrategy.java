package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.BehaviorKind;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonValue;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.JsonValuePsStrategy;

public class StandardSqlJsonValuePsStrategy implements JsonValuePsStrategy {

    @Override
    public PsDto handle(JsonValue jsonValue, PreparedStatementRenderer renderer, AstContext ctx) {
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
        return new PsDto(sql.toString(), parameters);
    }
}
