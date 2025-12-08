package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import lan.tlab.r4j.jdsql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.jdsql.ast.dml.statement.MergeStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.MergeStatementPsStrategy;

/**
 * MySQL-specific PreparedStatement strategy for MERGE statements.
 * Converts MERGE to INSERT ... ON DUPLICATE KEY UPDATE syntax with proper parameter binding.
 */
public class MySqlMergeStatementPsStrategy implements MergeStatementPsStrategy {
    @Override
    public PreparedStatementSpec handle(MergeStatement stmt, PreparedStatementRenderer renderer, AstContext ctx) {
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
                + renderer.getEscapeStrategy().apply(stmt.getTargetTable().name());

        // Column list
        if (!insertAction.columns().isEmpty()) {
            List<String> columns = new ArrayList<>();
            for (var col : insertAction.columns()) {
                columns.add(renderer.getEscapeStrategy().apply(col.column()));
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
                PreparedStatementSpec exprDto = expr.accept(renderer, selectCtx);
                selectExprs.add(exprDto.sql());
                params.addAll(exprDto.parameters());
            }
            sql += String.join(", ", selectExprs);
        } else {
            // Fallback: build column references from source
            List<String> selectExprs = new ArrayList<>();
            for (var col : insertAction.columns()) {
                ColumnReference sourceCol = ColumnReference.of(sourceAlias, col.column());
                PreparedStatementSpec colDto = sourceCol.accept(renderer, selectCtx);
                selectExprs.add(colDto.sql());
            }
            sql += String.join(", ", selectExprs);
        }

        // FROM source
        PreparedStatementSpec usingDto = stmt.getUsing().accept(renderer, ctx);
        sql += " FROM " + usingDto.sql();
        params.addAll(usingDto.parameters());

        // ON DUPLICATE KEY UPDATE (if we have WHEN MATCHED)
        if (updateAction != null) {
            sql += " ON DUPLICATE KEY UPDATE ";

            List<String> updateClauses = new ArrayList<>();
            for (UpdateItem item : updateAction.updateItems()) {
                PreparedStatementSpec colDto = item.column().accept(renderer, ctx);
                String columnName = colDto.sql();

                // Check if value is a ColumnReference to use VALUES()
                if (item.value() instanceof ColumnReference colRef) {
                    String escapedColName = renderer.getEscapeStrategy().apply(colRef.column());
                    updateClauses.add(columnName + " = VALUES(" + escapedColName + ")");
                } else {
                    // For literals and other expressions, use parameterized value
                    PreparedStatementSpec valDto = item.value().accept(renderer, ctx);
                    updateClauses.add(columnName + " = " + valDto.sql());
                    params.addAll(valDto.parameters());
                }
            }
            sql += String.join(", ", updateClauses);
        }

        return new PreparedStatementSpec(sql, params);
    }

    private String getSourceAlias(lan.tlab.r4j.jdsql.ast.core.expression.set.TableExpression source) {
        if (source instanceof lan.tlab.r4j.jdsql.ast.core.identifier.TableIdentifier tableId) {
            return tableId.getTableReference();
        } else if (source instanceof lan.tlab.r4j.jdsql.ast.core.expression.set.AliasedTableExpression aliased) {
            return aliased.getTableReference();
        }
        return "src"; // fallback
    }
}
