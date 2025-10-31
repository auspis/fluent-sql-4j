package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonValue;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.JsonValuePsStrategy;

public class DefaultJsonValuePsStrategy implements JsonValuePsStrategy {

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

        if (jsonValue.onEmptyBehavior() != null) {
            sql.append(" ");
            if (jsonValue.onEmptyBehavior()
                            == lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.BehaviorKind.DEFAULT
                    && jsonValue.onEmptyDefault() != null) {
                sql.append("DEFAULT ").append(jsonValue.onEmptyDefault());
            } else {
                sql.append(jsonValue.onEmptyBehavior().toSql());
            }
            sql.append(" ON EMPTY");
        }

        if (jsonValue.onErrorBehavior() != null) {
            sql.append(" ").append(jsonValue.onErrorBehavior().toSql()).append(" ON ERROR");
        }

        sql.append(")");
        return new PsDto(sql.toString(), parameters);
    }
}
