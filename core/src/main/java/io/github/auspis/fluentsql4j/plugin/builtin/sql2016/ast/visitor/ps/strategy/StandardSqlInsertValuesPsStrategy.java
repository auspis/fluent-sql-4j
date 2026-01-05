package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData.InsertValues;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.InsertValuesPsStrategy;

public class StandardSqlInsertValuesPsStrategy implements InsertValuesPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            InsertValues insertValues, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        List<String> placeholders = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (var expr : insertValues.valueExpressions()) {
            if (expr instanceof Literal<?> literal) {
                placeholders.add("?");
                params.add(literal.value());
            } else {
                // Fallback for non-literal expressions
                placeholders.add("?");
                params.add(null);
            }
        }
        String sql = String.join(", ", placeholders);
        return new PreparedStatementSpec(sql, params);
    }
}
