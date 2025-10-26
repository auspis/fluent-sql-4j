package lan.tlab.r4j.sql.ast.visitor.sql.dialect.mysql.strategy.statement;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.statement.dml.MergeStatement;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction.WhenNotMatchedInsert;
import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement.MergeStatementRenderStrategy;

/**
 * MySQL-specific implementation of MERGE statement rendering.
 * <p>
 * MySQL does not support the standard SQL MERGE statement. Instead, this strategy
 * converts MERGE INTO ... USING ... ON ... WHEN MATCHED ... WHEN NOT MATCHED ...
 * into MySQL's INSERT ... ON DUPLICATE KEY UPDATE syntax.
 * <p>
 * <b>Example transformation:</b>
 * <pre>
 * Standard SQL:
 *   MERGE INTO users AS tgt
 *   USING users_updates AS src
 *   ON tgt.id = src.id
 *   WHEN MATCHED THEN UPDATE SET name = src.name, age = src.age
 *   WHEN NOT MATCHED THEN INSERT (id, name, age) VALUES (src.id, src.name, src.age)
 *
 * MySQL equivalent:
 *   INSERT INTO users (id, name, age)
 *   SELECT src.id, src.name, src.age FROM users_updates AS src
 *   ON DUPLICATE KEY UPDATE name = VALUES(name), age = VALUES(age)
 * </pre>
 * <p>
 * <b>Limitations:</b>
 * <ul>
 *   <li>Only supports simple MERGE patterns with one WHEN MATCHED UPDATE and one WHEN NOT MATCHED INSERT</li>
 *   <li>Does not support WHEN MATCHED DELETE</li>
 *   <li>Does not support conditional WHEN clauses (AND conditions)</li>
 *   <li>Requires the target table to have a PRIMARY KEY or UNIQUE constraint on the ON condition columns</li>
 * </ul>
 */
public class MySqlMergeStatementRenderStrategy implements MergeStatementRenderStrategy {

    @Override
    public String render(MergeStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        // Find WHEN NOT MATCHED INSERT action (required for MySQL)
        WhenNotMatchedInsert insertAction = statement.getActions().stream()
                .filter(WhenNotMatchedInsert.class::isInstance)
                .map(WhenNotMatchedInsert.class::cast)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("MySQL MERGE requires a WHEN NOT MATCHED THEN INSERT clause"));

        // Find WHEN MATCHED UPDATE action (optional for MySQL)
        WhenMatchedUpdate updateAction = statement.getActions().stream()
                .filter(WhenMatchedUpdate.class::isInstance)
                .map(WhenMatchedUpdate.class::cast)
                .findFirst()
                .orElse(null);

        StringBuilder sql = new StringBuilder("INSERT INTO ");

        // Render target table
        sql.append(statement.getTargetTable().accept(sqlRenderer, ctx));

        // Render column list from WHEN NOT MATCHED INSERT
        if (!insertAction.columns().isEmpty()) {
            String columns = insertAction.columns().stream()
                    .map(col -> sqlRenderer.getEscapeStrategy().apply(col.getColumn()))
                    .collect(Collectors.joining(", "));
            sql.append(" (").append(columns).append(")");
        }

        // Extract source alias - handle both TableIdentifier and AliasedTableExpression
        String sourceAlias = getSourceAlias(statement.getUsing().source());

        // Render source as SELECT statement
        sql.append(" SELECT ");

        // Select the values from the insert action's insertData
        if (insertAction.insertData()
                instanceof lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertValues insertValues) {
            String selectColumns = insertValues.getValueExpressions().stream()
                    .map(expr -> expr.accept(sqlRenderer, ctx))
                    .collect(Collectors.joining(", "));
            sql.append(selectColumns);
        } else {
            // Fallback to using column references from source
            String selectColumns = insertAction.columns().stream()
                    .map(col -> {
                        // Use column reference with source table alias
                        ColumnReference sourceCol = ColumnReference.of(sourceAlias, col.getColumn());
                        return sourceCol.accept(sqlRenderer, ctx);
                    })
                    .collect(Collectors.joining(", "));
            sql.append(selectColumns);
        }

        // Render FROM using clause
        sql.append(" FROM ");
        sql.append(statement.getUsing().accept(sqlRenderer, ctx));

        // Render ON DUPLICATE KEY UPDATE clause if there's a WHEN MATCHED UPDATE action
        if (updateAction != null) {
            sql.append(" ON DUPLICATE KEY UPDATE ");

            String updates = updateAction.updateItems().stream()
                    .map(item -> renderUpdateItemForMySql(item, sqlRenderer, ctx))
                    .collect(Collectors.joining(", "));

            sql.append(updates);
        }

        return sql.toString();
    }

    /**
     * Extracts the alias from a TableExpression (TableIdentifier or AliasedTableExpression).
     */
    private String getSourceAlias(lan.tlab.r4j.sql.ast.expression.set.TableExpression source) {
        if (source instanceof lan.tlab.r4j.sql.ast.identifier.TableIdentifier tableId) {
            return tableId.getTableReference();
        } else if (source instanceof lan.tlab.r4j.sql.ast.expression.set.AliasedTableExpression aliased) {
            return aliased.getTableReference();
        }
        return "src"; // fallback
    }

    /**
     * Renders an update item for MySQL's ON DUPLICATE KEY UPDATE clause.
     * Uses VALUES(column) to reference the attempted insert values.
     */
    private String renderUpdateItemForMySql(UpdateItem item, SqlRenderer sqlRenderer, AstContext ctx) {
        String columnName = item.getColumn().accept(sqlRenderer, ctx);
        // For MySQL ON DUPLICATE KEY UPDATE, we use VALUES(column) to reference the insert value
        // However, the DSL typically uses column references like "src.name"
        // We need to extract just the column name and wrap it in VALUES()
        String valueExpr = item.getValue().accept(sqlRenderer, ctx);

        // Extract column name from expressions like `src`.`name` or src.name
        // For simplicity, if the value is a ColumnReference, use VALUES(column_name)
        if (item.getValue() instanceof ColumnReference colRef) {
            String escapedColName = sqlRenderer.getEscapeStrategy().apply(colRef.getColumn());
            return columnName + " = VALUES(" + escapedColName + ")";
        }

        // For literal values or expressions, use as-is
        return columnName + " = " + valueExpr;
    }
}
