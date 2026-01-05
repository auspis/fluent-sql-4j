package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedUpdate;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import io.github.auspis.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.auspis.fluentsql4j.ast.dml.statement.MergeStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.MergeStatementPsStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL-specific PreparedStatement strategy for MERGE statements.
 * Converts MERGE to INSERT ... ON DUPLICATE KEY UPDATE syntax with proper parameter binding.
 */
public class MySqlMergeStatementPsStrategy implements MergeStatementPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            MergeStatement stmt, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        List<Object> params = new ArrayList<>();

        // Find WHEN NOT MATCHED INSERT action (required for MySQL)
        WhenNotMatchedInsert insertAction = stmt.getActions().stream()
                .filter(WhenNotMatchedInsert.class::isInstance)
                .map(WhenNotMatchedInsert.class::cast)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("MySQL MERGE requires a WHEN NOT MATCHED THEN INSERT clause"));

        // Find WHEN MATCHED UPDATE action (optional for MySQL)
        WhenMatchedUpdate updateAction = stmt.getActions().stream()
                .filter(WhenMatchedUpdate.class::isInstance)
                .map(WhenMatchedUpdate.class::cast)
                .findFirst()
                .orElse(null);

        // INSERT INTO target_table
        String sql = "INSERT INTO "
                + astToPsSpecVisitor
                        .getEscapeStrategy()
                        .apply(stmt.getTargetTable().name());

        // Column list
        if (!insertAction.columns().isEmpty()) {
            List<String> columns = new ArrayList<>();
            for (var col : insertAction.columns()) {
                columns.add(astToPsSpecVisitor.getEscapeStrategy().apply(col.column()));
            }
            sql += " (" + String.join(", ", columns) + ")";
        }

        // SELECT values FROM source
        sql += " SELECT ";

        // Extract source alias
        String sourceAlias = getSourceAlias(stmt.getUsing().source());

        // Use JOIN_ON scope to qualify column references
        AstContext selectCtx = new AstContext(AstContext.Feature.JOIN_ON);

        // Render the values from insertData
        if (insertAction.insertData() instanceof InsertData.InsertValues insertValues) {
            List<String> selectExprs = new ArrayList<>();
            for (var expr : insertValues.valueExpressions()) {
                PreparedStatementSpec exprDto = expr.accept(astToPsSpecVisitor, selectCtx);
                selectExprs.add(exprDto.sql());
                params.addAll(exprDto.parameters());
            }
            sql += String.join(", ", selectExprs);
        } else {
            // Fallback: build column references from source
            List<String> selectExprs = new ArrayList<>();
            for (var col : insertAction.columns()) {
                ColumnReference sourceCol = ColumnReference.of(sourceAlias, col.column());
                PreparedStatementSpec colDto = sourceCol.accept(astToPsSpecVisitor, selectCtx);
                selectExprs.add(colDto.sql());
            }
            sql += String.join(", ", selectExprs);
        }

        // FROM source
        PreparedStatementSpec usingDto = stmt.getUsing().accept(astToPsSpecVisitor, ctx);
        sql += " FROM " + usingDto.sql();
        params.addAll(usingDto.parameters());

        // ON DUPLICATE KEY UPDATE (if we have WHEN MATCHED)
        if (updateAction != null) {
            sql += " ON DUPLICATE KEY UPDATE ";

            List<String> updateClauses = new ArrayList<>();
            for (UpdateItem item : updateAction.updateItems()) {
                PreparedStatementSpec colDto = item.column().accept(astToPsSpecVisitor, ctx);
                String columnName = colDto.sql();

                // Check if value is a ColumnReference to use VALUES()
                if (item.value() instanceof ColumnReference colRef) {
                    String escapedColName =
                            astToPsSpecVisitor.getEscapeStrategy().apply(colRef.column());
                    updateClauses.add(columnName + " = VALUES(" + escapedColName + ")");
                } else {
                    // For literals and other expressions, use parameterized value
                    PreparedStatementSpec valDto = item.value().accept(astToPsSpecVisitor, ctx);
                    updateClauses.add(columnName + " = " + valDto.sql());
                    params.addAll(valDto.parameters());
                }
            }
            sql += String.join(", ", updateClauses);
        }

        return new PreparedStatementSpec(sql, params);
    }

    private String getSourceAlias(io.github.auspis.fluentsql4j.ast.core.expression.set.TableExpression source) {
        if (source instanceof io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier tableId) {
            return tableId.getTableReference();
        } else if (source
                instanceof io.github.auspis.fluentsql4j.ast.core.expression.set.AliasedTableExpression aliased) {
            return aliased.getTableReference();
        }
        return "src"; // fallback
    }
}
