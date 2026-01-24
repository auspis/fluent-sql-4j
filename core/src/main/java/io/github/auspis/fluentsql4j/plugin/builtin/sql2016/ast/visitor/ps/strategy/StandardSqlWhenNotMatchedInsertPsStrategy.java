package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dml.component.InsertData;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.WhenNotMatchedInsertPsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlWhenNotMatchedInsertPsStrategy implements WhenNotMatchedInsertPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            WhenNotMatchedInsert item, AstToPreparedStatementSpecVisitor visitor, AstContext ctx) {
        List<Object> allParameters = new ArrayList<>();
        StringBuilder sql = new StringBuilder("WHEN NOT MATCHED");

        if (item.condition() != null) {
            PreparedStatementSpec conditionDto = item.condition().accept(visitor, ctx);
            allParameters.addAll(conditionDto.parameters());
            sql.append(" AND ").append(conditionDto.sql());
        }

        sql.append(" THEN INSERT");

        if (!item.columns().isEmpty()) {
            String columns = item.columns().stream()
                    .map(col -> {
                        PreparedStatementSpec colDto = col.accept(visitor, ctx);
                        allParameters.addAll(colDto.parameters());
                        return colDto.sql();
                    })
                    .collect(java.util.stream.Collectors.joining(", "));
            sql.append(" (").append(columns).append(")");
        }

        // For INSERT VALUES in MERGE, we need to render the expressions directly
        // since they can be column references from the source table
        if (item.insertData() instanceof InsertData.InsertValues insertValues) {
            List<String> valueClauses = new ArrayList<>();
            for (var expr : insertValues.valueExpressions()) {
                PreparedStatementSpec exprDto = expr.accept(visitor, ctx);
                valueClauses.add(exprDto.sql());
                allParameters.addAll(exprDto.parameters());
            }
            sql.append(" VALUES (").append(String.join(", ", valueClauses)).append(")");
        } else {
            // Fallback for other insert data types
            PreparedStatementSpec insertDataDto = item.insertData().accept(visitor, ctx);
            allParameters.addAll(insertDataDto.parameters());
            sql.append(" ").append(insertDataDto.sql());
        }

        return new PreparedStatementSpec(sql.toString(), allParameters);
    }
}
