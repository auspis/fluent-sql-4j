package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.dml.component.InsertData;
import lan.tlab.r4j.sql.ast.dml.component.MergeAction;
import lan.tlab.r4j.sql.ast.dml.component.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.sql.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import lan.tlab.r4j.sql.ast.dml.statement.MergeStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.MergeStatementPsStrategy;

public class StandardSqlMergeStatementPsStrategy implements MergeStatementPsStrategy {
    @Override
    public PsDto handle(MergeStatement stmt, PreparedStatementRenderer renderer, AstContext ctx) {
        List<Object> params = new ArrayList<>();

        // MERGE INTO target
        PsDto targetDto = stmt.getTargetTable().accept(renderer, ctx);
        String sql = "MERGE INTO " + targetDto.sql();
        params.addAll(targetDto.parameters());

        // USING source
        PsDto usingDto = stmt.getUsing().accept(renderer, ctx);
        sql += " USING " + usingDto.sql();
        params.addAll(usingDto.parameters());

        // ON condition - use JOIN_ON scope to qualify column references
        AstContext onCtx = new AstContext(AstContext.Scope.JOIN_ON);
        PsDto onDto = stmt.getOnCondition().accept(renderer, onCtx);
        sql += " ON " + onDto.sql();
        params.addAll(onDto.parameters());

        // WHEN MATCHED / WHEN NOT MATCHED actions
        for (MergeAction action : stmt.getActions()) {
            if (action instanceof WhenMatchedUpdate whenMatched) {
                sql += " WHEN MATCHED";
                if (whenMatched.condition() != null) {
                    PsDto condDto = whenMatched.condition().accept(renderer, onCtx);
                    sql += " AND " + condDto.sql();
                    params.addAll(condDto.parameters());
                }
                sql += " THEN UPDATE SET ";

                List<String> updateClauses = new ArrayList<>();
                for (var item : whenMatched.updateItems()) {
                    PsDto colDto = item.column().accept(renderer, ctx);
                    PsDto valDto = item.value().accept(renderer, onCtx);
                    updateClauses.add(colDto.sql() + " = " + valDto.sql());
                    params.addAll(valDto.parameters());
                }
                sql += String.join(", ", updateClauses);

            } else if (action instanceof WhenNotMatchedInsert whenNotMatched) {
                sql += " WHEN NOT MATCHED";
                if (whenNotMatched.condition() != null) {
                    PsDto condDto = whenNotMatched.condition().accept(renderer, onCtx);
                    sql += " AND " + condDto.sql();
                    params.addAll(condDto.parameters());
                }
                sql += " THEN INSERT";

                if (!whenNotMatched.columns().isEmpty()) {
                    List<String> columns = new ArrayList<>();
                    for (var col : whenNotMatched.columns()) {
                        PsDto colDto = col.accept(renderer, ctx);
                        columns.add(colDto.sql());
                    }
                    sql += " (" + String.join(", ", columns) + ")";
                }

                // For INSERT VALUES in MERGE, we need to render the expressions directly
                // since they can be column references from the source table
                if (whenNotMatched.insertData() instanceof InsertData.InsertValues insertValues) {
                    List<String> valueClauses = new ArrayList<>();
                    for (var expr : insertValues.valueExpressions()) {
                        PsDto exprDto = expr.accept(renderer, onCtx);
                        valueClauses.add(exprDto.sql());
                        params.addAll(exprDto.parameters());
                    }
                    sql += " VALUES (" + String.join(", ", valueClauses) + ")";
                } else {
                    // Fallback for other insert data types
                    PsDto insertDto = whenNotMatched.insertData().accept(renderer, onCtx);
                    sql += " VALUES (" + insertDto.sql() + ")";
                    params.addAll(insertDto.parameters());
                }
            } else if (action instanceof MergeAction.WhenMatchedDelete whenMatchedDelete) {
                // WHEN MATCHED DELETE
                sql += " WHEN MATCHED";
                if (whenMatchedDelete.condition() != null) {
                    PsDto condDto = whenMatchedDelete.condition().accept(renderer, onCtx);
                    sql += " AND " + condDto.sql();
                    params.addAll(condDto.parameters());
                }
                sql += " THEN DELETE";
            } else {
                // Unknown action type - shouldn't happen
                throw new IllegalArgumentException("Unknown merge action type: " + action.getClass());
            }
        }

        return new PsDto(sql, params);
    }
}
