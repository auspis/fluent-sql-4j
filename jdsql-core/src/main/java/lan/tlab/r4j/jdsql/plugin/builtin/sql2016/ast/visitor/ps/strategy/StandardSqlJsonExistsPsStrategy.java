package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.BehaviorKind;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonExists;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.JsonExistsPsStrategy;

public class StandardSqlJsonExistsPsStrategy implements JsonExistsPsStrategy {

    @Override
    public PsDto handle(JsonExists jsonExists, PreparedStatementRenderer renderer, AstContext ctx) {
        var documentResult = jsonExists.jsonDocument().accept(renderer, ctx);
        var pathResult = jsonExists.path().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(documentResult.parameters());
        parameters.addAll(pathResult.parameters());

        StringBuilder sql = new StringBuilder("JSON_EXISTS(");
        sql.append(documentResult.sql());
        sql.append(", ");
        sql.append(pathResult.sql());

        if (jsonExists.onErrorBehavior() != BehaviorKind.NONE) {
            sql.append(" ").append(jsonExists.onErrorBehavior().name()).append(" ON ERROR");
        }

        sql.append(")");
        return new PsDto(sql.toString(), parameters);
    }
}
