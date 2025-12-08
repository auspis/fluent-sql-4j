package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import lan.tlab.r4j.jdsql.ast.dml.statement.MergeStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.MergeStatementPsStrategy;

public class StandardSqlMergeStatementPsStrategy implements MergeStatementPsStrategy {
    @Override
    public PreparedStatementSpec handle(MergeStatement stmt, PreparedStatementRenderer renderer, AstContext ctx) {
        List<Object> params = new ArrayList<>();

        // MERGE INTO target
        PreparedStatementSpec targetDto = stmt.getTargetTable().accept(renderer, ctx);
        String sql = "MERGE INTO " + targetDto.sql();
        params.addAll(targetDto.parameters());

        // USING source
        PreparedStatementSpec usingDto = stmt.getUsing().accept(renderer, ctx);
        sql += " USING " + usingDto.sql();
        params.addAll(usingDto.parameters());

        // ON condition - use JOIN_ON scope to qualify column references
        AstContext onCtx = new AstContext(AstContext.Feature.JOIN_ON);
        PreparedStatementSpec onDto = stmt.getOnCondition().accept(renderer, onCtx);
        sql += " ON " + onDto.sql();
        params.addAll(onDto.parameters());

        // WHEN MATCHED / WHEN NOT MATCHED actions
        for (MergeAction action : stmt.getActions()) {
            if (action instanceof WhenMatchedUpdate whenMatched) {
                sql += " WHEN MATCHED";
                if (whenMatched.condition() != null) {
                    PreparedStatementSpec condDto = whenMatched.condition().accept(renderer, onCtx);
                    sql += " AND " + condDto.sql();
                    params.addAll(condDto.parameters());
                }
                sql += " THEN UPDATE SET ";

                List<String> updateClauses = new ArrayList<>();
                for (var item : whenMatched.updateItems()) {
                    PreparedStatementSpec colDto = item.column().accept(renderer, ctx);
                    PreparedStatementSpec valDto = item.value().accept(renderer, onCtx);
                    updateClauses.add(colDto.sql() + " = " + valDto.sql());
                    params.addAll(valDto.parameters());
                }
                sql += String.join(", ", updateClauses);

            } else if (action instanceof WhenNotMatchedInsert whenNotMatched) {
                sql += " WHEN NOT MATCHED";
                if (whenNotMatched.condition() != null) {
                    PreparedStatementSpec condDto = whenNotMatched.condition().accept(renderer, onCtx);
                    sql += " AND " + condDto.sql();
                    params.addAll(condDto.parameters());
                }
                sql += " THEN INSERT";

                if (!whenNotMatched.columns().isEmpty()) {
                    List<String> columns = new ArrayList<>();
                    for (var col : whenNotMatched.columns()) {
                        PreparedStatementSpec colDto = col.accept(renderer, ctx);
                        columns.add(colDto.sql());
                    }
                    sql += " (" + String.join(", ", columns) + ")";
                }

                // For INSERT VALUES in MERGE, we need to render the expressions directly
                // since they can be column references from the source table
                if (whenNotMatched.insertData() instanceof InsertData.InsertValues insertValues) {
                    List<String> valueClauses = new ArrayList<>();
                    for (var expr : insertValues.valueExpressions()) {
                        PreparedStatementSpec exprDto = expr.accept(renderer, onCtx);
                        valueClauses.add(exprDto.sql());
                        params.addAll(exprDto.parameters());
                    }
                    sql += " VALUES (" + String.join(", ", valueClauses) + ")";
                } else {
                    // Fallback for other insert data types
                    PreparedStatementSpec insertDto =
                            whenNotMatched.insertData().accept(renderer, onCtx);
                    sql += " VALUES (" + insertDto.sql() + ")";
                    params.addAll(insertDto.parameters());
                }
            } else if (action instanceof MergeAction.WhenMatchedDelete whenMatchedDelete) {
                // WHEN MATCHED DELETE
                sql += " WHEN MATCHED";
                if (whenMatchedDelete.condition() != null) {
                    PreparedStatementSpec condDto =
                            whenMatchedDelete.condition().accept(renderer, onCtx);
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
