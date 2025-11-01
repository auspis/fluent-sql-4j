package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonQuery;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.JsonQueryPsStrategy;

public class DefaultJsonQueryPsStrategy implements JsonQueryPsStrategy {

    @Override
    public PsDto handle(JsonQuery jsonQuery, PreparedStatementRenderer renderer, AstContext ctx) {
        var documentResult = jsonQuery.jsonDocument().accept(renderer, ctx);
        var pathResult = jsonQuery.path().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(documentResult.parameters());
        parameters.addAll(pathResult.parameters());

        StringBuilder sql = new StringBuilder("JSON_QUERY(");
        sql.append(documentResult.sql());
        sql.append(", ");
        sql.append(pathResult.sql());

        if (jsonQuery.returningType() != null) {
            sql.append(" RETURNING ").append(jsonQuery.returningType());
        }

        if (jsonQuery.wrapperBehavior()
                != lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.WrapperBehavior.NONE) {
            sql.append(" ").append(jsonQuery.wrapperBehavior().name().replace("_", " "));
        }

        if (jsonQuery.onEmptyBehavior().kind()
                != lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.BehaviorKind.NULL) {
            sql.append(" ");
            if (jsonQuery.onEmptyBehavior().kind()
                            == lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.BehaviorKind.DEFAULT
                    && jsonQuery.onEmptyBehavior().defaultValue() != null) {
                sql.append("DEFAULT ").append(jsonQuery.onEmptyBehavior().defaultValue());
            } else {
                sql.append(jsonQuery.onEmptyBehavior().kind().name());
            }
            sql.append(" ON EMPTY");
        }

        if (jsonQuery.onErrorBehavior().kind()
                != lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.BehaviorKind.NULL) {
            sql.append(" ").append(jsonQuery.onErrorBehavior().kind().name()).append(" ON ERROR");
        }

        sql.append(")");
        return new PsDto(sql.toString(), parameters);
    }
}
