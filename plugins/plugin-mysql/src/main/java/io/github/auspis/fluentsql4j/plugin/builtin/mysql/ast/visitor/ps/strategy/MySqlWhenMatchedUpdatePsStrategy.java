package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedUpdate;
import io.github.auspis.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.WhenMatchedUpdatePsStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL-specific strategy for WHEN MATCHED THEN UPDATE in MERGE statements.
 * Renders the ON DUPLICATE KEY UPDATE clause with MySQL-specific VALUES() function.
 */
public class MySqlWhenMatchedUpdatePsStrategy implements WhenMatchedUpdatePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            WhenMatchedUpdate item, AstToPreparedStatementSpecVisitor visitor, AstContext ctx) {
        List<Object> allParameters = new ArrayList<>();
        StringBuilder sql = new StringBuilder("ON DUPLICATE KEY UPDATE ");

        List<String> updateClauses = buildUpdateClauses(item, visitor, ctx, allParameters);
        sql.append(String.join(", ", updateClauses));

        return new PreparedStatementSpec(sql.toString(), allParameters);
    }

    private List<String> buildUpdateClauses(
            WhenMatchedUpdate item, AstToPreparedStatementSpecVisitor visitor, AstContext ctx, List<Object> params) {
        List<String> updateClauses = new ArrayList<>();

        for (UpdateItem updateItem : item.updateItems()) {
            PreparedStatementSpec colDto = updateItem.column().accept(visitor, ctx);
            String columnName = colDto.sql();

            // MySQL-specific: Use VALUES() function when value is a ColumnReference
            if (updateItem.value() instanceof ColumnReference colRef) {
                String escapedColName = visitor.getEscapeStrategy().apply(colRef.column());
                updateClauses.add(columnName + " = VALUES(" + escapedColName + ")");
            } else {
                // For literals and other expressions, use parameterized value
                PreparedStatementSpec valDto = updateItem.value().accept(visitor, ctx);
                updateClauses.add(columnName + " = " + valDto.sql());
                params.addAll(valDto.parameters());
            }
        }

        return updateClauses;
    }
}
