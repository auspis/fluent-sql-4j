package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.WhenNotMatchedInsertPsStrategy;
import io.github.auspis.fluentsql4j.dsl.util.ColumnReferenceUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL-specific strategy for WHEN NOT MATCHED THEN INSERT in MERGE statements.
 * Renders the column list and SELECT portion of INSERT...ON DUPLICATE KEY UPDATE syntax.
 */
public class MySqlWhenNotMatchedInsertPsStrategy implements WhenNotMatchedInsertPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            WhenNotMatchedInsert item, AstToPreparedStatementSpecVisitor visitor, AstContext ctx) {
        List<Object> allParameters = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        // MySQL: Build column list
        if (!item.columns().isEmpty()) {
            List<String> columns = new ArrayList<>();
            for (var col : item.columns()) {
                columns.add(visitor.getEscapeStrategy().apply(col.column()));
            }
            sql.append("(").append(String.join(", ", columns)).append(")");
        }

        // MySQL: Build SELECT clause
        sql.append(" SELECT ");
        List<String> selectExprs = buildSelectExpressions(item, visitor, allParameters);
        sql.append(String.join(", ", selectExprs));

        return new PreparedStatementSpec(sql.toString(), allParameters);
    }

    private List<String> buildSelectExpressions(
            WhenNotMatchedInsert item, AstToPreparedStatementSpecVisitor visitor, List<Object> params) {
        List<String> selectExprs = new ArrayList<>();
        AstContext selectCtx = new AstContext(AstContext.Feature.JOIN_ON);

        if (item.insertData() instanceof InsertData.InsertValues insertValues) {
            // Render value expressions directly (can be column references or literals)
            for (var expr : insertValues.valueExpressions()) {
                PreparedStatementSpec exprDto = expr.accept(visitor, selectCtx);
                selectExprs.add(exprDto.sql());
                params.addAll(exprDto.parameters());
            }
        } else {
            // Fallback: build column references from source
            String sourceAlias = "src"; // Default fallback
            for (var col : item.columns()) {
                ColumnReference sourceCol =
                        ColumnReferenceUtil.createValidatedWithTrustedTable(sourceAlias, col.column());
                PreparedStatementSpec colDto = sourceCol.accept(visitor, selectCtx);
                selectExprs.add(colDto.sql());
            }
        }

        return selectExprs;
    }
}
