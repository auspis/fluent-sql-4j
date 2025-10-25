package lan.tlab.r4j.sql.ast.visitor.sql.dialect.mysql.strategy.statement;

import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.statement.dml.MergeStatement;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction.WhenNotMatchedInsert;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement.MergeStatementRenderStrategy;

/**
 * MySQL-specific MERGE statement render strategy.
 * <p>
 * MySQL does not support the standard SQL MERGE statement. Instead, this strategy
 * translates MERGE into MySQL's INSERT ... ON DUPLICATE KEY UPDATE syntax.
 * <p>
 * <b>Limitations:</b>
 * <ul>
 *   <li>Only supports simple table-to-table merge (no subqueries in USING clause)</li>
 *   <li>Only supports WHEN MATCHED THEN UPDATE and WHEN NOT MATCHED THEN INSERT</li>
 *   <li>Does not support WHEN MATCHED THEN DELETE</li>
 *   <li>Does not support conditional WHEN clauses (AND conditions)</li>
 *   <li>Requires a PRIMARY KEY or UNIQUE constraint on the target table</li>
 * </ul>
 * <p>
 * <b>SQL Translation:</b>
 * <pre>{@code
 * Standard SQL:
 * MERGE INTO target USING source ON target.id = source.id
 * WHEN MATCHED THEN UPDATE SET target.name = source.name
 * WHEN NOT MATCHED THEN INSERT (id, name) VALUES (source.id, source.name)
 *
 * MySQL Output:
 * INSERT INTO target (id, name)
 * SELECT source.id, source.name FROM source
 * ON DUPLICATE KEY UPDATE name = VALUES(name)
 * }</pre>
 */
public class MySqlMergeStatementRenderStrategy implements MergeStatementRenderStrategy {

    @Override
    public String render(MergeStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        // Extract WHEN MATCHED UPDATE and WHEN NOT MATCHED INSERT actions
        WhenMatchedUpdate updateAction = null;
        WhenNotMatchedInsert insertAction = null;

        for (MergeAction action : statement.getActions()) {
            if (action instanceof WhenMatchedUpdate matched) {
                updateAction = matched;
            } else if (action instanceof WhenNotMatchedInsert notMatched) {
                insertAction = notMatched;
            }
        }

        // We need at least the insert action to generate valid SQL
        if (insertAction == null) {
            throw new IllegalArgumentException(
                    "MySQL MERGE requires WHEN NOT MATCHED THEN INSERT clause to determine columns");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(statement.getTargetTable().accept(sqlRenderer, ctx));

        // Extract columns from WHEN NOT MATCHED INSERT
        List<ColumnReference> columns = insertAction.columns();
        InsertData insertData = insertAction.insertData();

        // Render column list
        String columnList = columns.stream()
                .map(col -> sqlRenderer.getEscapeStrategy().escapeIdentifier(col.getName()))
                .collect(Collectors.joining(", "));
        sql.append(" (").append(columnList).append(")");

        // Render SELECT from source
        sql.append(" SELECT ");
        String selectList = insertData.accept(sqlRenderer, ctx);
        // Remove VALUES(...) wrapper if present
        if (selectList.startsWith("VALUES (") && selectList.endsWith(")")) {
            selectList = selectList.substring(8, selectList.length() - 1);
        }
        sql.append(selectList);

        sql.append(" FROM ");
        sql.append(statement.getUsing().accept(sqlRenderer, ctx));

        // Add ON DUPLICATE KEY UPDATE clause if WHEN MATCHED exists
        if (updateAction != null) {
            sql.append(" ON DUPLICATE KEY UPDATE ");

            String updates = updateAction.updateItems().stream()
                    .map(item -> {
                        String columnName = sqlRenderer
                                .getEscapeStrategy()
                                .escapeIdentifier(item.getColumn().getName());
                        return columnName + " = VALUES(" + columnName + ")";
                    })
                    .collect(Collectors.joining(", "));

            sql.append(updates);
        }

        return sql.toString();
    }
}
