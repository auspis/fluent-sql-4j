package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.BehaviorKind;
import io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.JsonExists;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.JsonExistsPsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlJsonExistsPsStrategy implements JsonExistsPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            JsonExists jsonExists, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        var documentResult = jsonExists.jsonDocument().accept(astToPsSpecVisitor, ctx);
        var pathResult = jsonExists.path().accept(astToPsSpecVisitor, ctx);

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
        return new PreparedStatementSpec(sql.toString(), parameters);
    }
}
