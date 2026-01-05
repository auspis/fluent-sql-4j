package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedUpdate;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import io.github.auspis.fluentsql4j.ast.dml.statement.MergeStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.MergeStatementPsStrategy;

public class StandardSqlMergeStatementPsStrategy implements MergeStatementPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            MergeStatement stmt, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        List<Object> params = new ArrayList<>();

        // MERGE INTO target
        PreparedStatementSpec targetDto = stmt.getTargetTable().accept(astToPsSpecVisitor, ctx);
        String sql = "MERGE INTO " + targetDto.sql();
        params.addAll(targetDto.parameters());

        // USING source
        PreparedStatementSpec usingDto = stmt.getUsing().accept(astToPsSpecVisitor, ctx);
        sql += " USING " + usingDto.sql();
        params.addAll(usingDto.parameters());

        // ON condition - use JOIN_ON scope to qualify column references
        AstContext onCtx = new AstContext(AstContext.Feature.JOIN_ON);
        PreparedStatementSpec onDto = stmt.getOnCondition().accept(astToPsSpecVisitor, onCtx);
        sql += " ON " + onDto.sql();
        params.addAll(onDto.parameters());

        // WHEN MATCHED / WHEN NOT MATCHED actions
        for (MergeAction action : stmt.getActions()) {
            if (action instanceof WhenMatchedUpdate whenMatched) {
                sql += " WHEN MATCHED";
                if (whenMatched.condition() != null) {
                    PreparedStatementSpec condDto = whenMatched.condition().accept(astToPsSpecVisitor, onCtx);
                    sql += " AND " + condDto.sql();
                    params.addAll(condDto.parameters());
                }
                sql += " THEN UPDATE SET ";

                List<String> updateClauses = new ArrayList<>();
                for (var item : whenMatched.updateItems()) {
                    PreparedStatementSpec colDto = item.column().accept(astToPsSpecVisitor, ctx);
                    PreparedStatementSpec valDto = item.value().accept(astToPsSpecVisitor, onCtx);
                    updateClauses.add(colDto.sql() + " = " + valDto.sql());
                    params.addAll(valDto.parameters());
                }
                sql += String.join(", ", updateClauses);

            } else if (action instanceof WhenNotMatchedInsert whenNotMatched) {
                sql += " WHEN NOT MATCHED";
                if (whenNotMatched.condition() != null) {
                    PreparedStatementSpec condDto = whenNotMatched.condition().accept(astToPsSpecVisitor, onCtx);
                    sql += " AND " + condDto.sql();
                    params.addAll(condDto.parameters());
                }
                sql += " THEN INSERT";

                if (!whenNotMatched.columns().isEmpty()) {
                    List<String> columns = new ArrayList<>();
                    for (var col : whenNotMatched.columns()) {
                        PreparedStatementSpec colDto = col.accept(astToPsSpecVisitor, ctx);
                        columns.add(colDto.sql());
                    }
                    sql += " (" + String.join(", ", columns) + ")";
                }

                // For INSERT VALUES in MERGE, we need to render the expressions directly
                // since they can be column references from the source table
                if (whenNotMatched.insertData() instanceof InsertData.InsertValues insertValues) {
                    List<String> valueClauses = new ArrayList<>();
                    for (var expr : insertValues.valueExpressions()) {
                        PreparedStatementSpec exprDto = expr.accept(astToPsSpecVisitor, onCtx);
                        valueClauses.add(exprDto.sql());
                        params.addAll(exprDto.parameters());
                    }
                    sql += " VALUES (" + String.join(", ", valueClauses) + ")";
                } else {
                    // Fallback for other insert data types
                    PreparedStatementSpec insertDto =
                            whenNotMatched.insertData().accept(astToPsSpecVisitor, onCtx);
                    sql += " VALUES (" + insertDto.sql() + ")";
                    params.addAll(insertDto.parameters());
                }
            } else if (action instanceof MergeAction.WhenMatchedDelete whenMatchedDelete) {
                // WHEN MATCHED DELETE
                sql += " WHEN MATCHED";
                if (whenMatchedDelete.condition() != null) {
                    PreparedStatementSpec condDto =
                            whenMatchedDelete.condition().accept(astToPsSpecVisitor, onCtx);
                    sql += " AND " + condDto.sql();
                    params.addAll(condDto.parameters());
                }
                sql += " THEN DELETE";
            } else {
                // Unknown action type - shouldn't happen
                throw new IllegalArgumentException("Unknown merge action type: " + action.getClass());
            }
        }

        return new PreparedStatementSpec(sql, params);
    }
}
