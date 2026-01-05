package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.function.json.BehaviorKind;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.JsonQuery;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.WrapperBehavior;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.JsonQueryPsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlJsonQueryPsStrategy implements JsonQueryPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            JsonQuery jsonQuery, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        var documentResult = jsonQuery.jsonDocument().accept(astToPsSpecVisitor, ctx);
        var pathResult = jsonQuery.path().accept(astToPsSpecVisitor, ctx);

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

        if (jsonQuery.wrapperBehavior() != WrapperBehavior.NONE) {
            sql.append(" ").append(jsonQuery.wrapperBehavior().name().replace("_", " "));
        }

        if (jsonQuery.onEmptyBehavior().kind() != BehaviorKind.NONE) {
            sql.append(" ");
            if (jsonQuery.onEmptyBehavior().kind() == BehaviorKind.DEFAULT
                    && jsonQuery.onEmptyBehavior().defaultValue() != null) {
                sql.append("DEFAULT ").append(jsonQuery.onEmptyBehavior().defaultValue());
            } else {
                sql.append(jsonQuery.onEmptyBehavior().kind().name());
            }
            sql.append(" ON EMPTY");
        }

        if (jsonQuery.onErrorBehavior() != BehaviorKind.NONE) {
            sql.append(" ").append(jsonQuery.onErrorBehavior().name()).append(" ON ERROR");
        }

        sql.append(")");
        return new PreparedStatementSpec(sql.toString(), parameters);
    }
}
