package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedDelete;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedUpdate;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import io.github.auspis.fluentsql4j.ast.dml.statement.MergeStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.MergeStatementPsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.WhenMatchedDeletePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.WhenMatchedUpdatePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.WhenNotMatchedInsertPsStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL-specific PreparedStatement strategy for MERGE statements.
 * Converts MERGE to INSERT ... ON DUPLICATE KEY UPDATE syntax with proper parameter binding.
 * Delegates action handling to MySQL-specific strategies for each WHEN clause.
 */
public class MySqlMergeStatementPsStrategy implements MergeStatementPsStrategy {

    private final WhenMatchedUpdatePsStrategy whenMatchedUpdateStrategy;
    private final WhenNotMatchedInsertPsStrategy whenNotMatchedInsertStrategy;
    private final WhenMatchedDeletePsStrategy whenMatchedDeleteStrategy;

    public MySqlMergeStatementPsStrategy() {
        this.whenMatchedUpdateStrategy = new MySqlWhenMatchedUpdatePsStrategy();
        this.whenNotMatchedInsertStrategy = new MySqlWhenNotMatchedInsertPsStrategy();
        this.whenMatchedDeleteStrategy = new MySqlWhenMatchedDeletePsStrategy();
    }

    @Override
    public PreparedStatementSpec handle(
            MergeStatement stmt, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        WhenNotMatchedInsert insertAction = findInsertAction(stmt);

        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        buildInsertIntoClause(sql, stmt, astToPsSpecVisitor);
        buildSelectAndFromClauses(sql, params, insertAction, stmt, astToPsSpecVisitor, ctx);
        buildActionClauses(sql, params, stmt, astToPsSpecVisitor, ctx);

        return new PreparedStatementSpec(sql.toString(), params);
    }

    private WhenNotMatchedInsert findInsertAction(MergeStatement stmt) {
        return stmt.getActions().stream()
                .filter(WhenNotMatchedInsert.class::isInstance)
                .map(WhenNotMatchedInsert.class::cast)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("MySQL MERGE requires a WHEN NOT MATCHED THEN INSERT clause"));
    }

    private void buildInsertIntoClause(
            StringBuilder sql, MergeStatement stmt, AstToPreparedStatementSpecVisitor astToPsSpecVisitor) {
        sql.append("INSERT INTO ")
                .append(astToPsSpecVisitor
                        .getEscapeStrategy()
                        .apply(stmt.getTargetTable().name()));
    }

    private void buildSelectAndFromClauses(
            StringBuilder sql,
            List<Object> params,
            WhenNotMatchedInsert insertAction,
            MergeStatement stmt,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx) {
        // Delegate to INSERT action strategy (returns column list + SELECT values)
        PreparedStatementSpec insertSpec = whenNotMatchedInsertStrategy.handle(insertAction, astToPsSpecVisitor, ctx);
        sql.append(" ").append(insertSpec.sql());
        params.addAll(insertSpec.parameters());

        // FROM source
        PreparedStatementSpec usingDto = stmt.getUsing().accept(astToPsSpecVisitor, ctx);
        sql.append(" FROM ").append(usingDto.sql());
        params.addAll(usingDto.parameters());
    }

    private void buildActionClauses(
            StringBuilder sql,
            List<Object> params,
            MergeStatement stmt,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx) {
        for (MergeAction action : stmt.getActions()) {
            if (action instanceof WhenMatchedUpdate whenMatched) {
                PreparedStatementSpec actionSpec =
                        whenMatchedUpdateStrategy.handle(whenMatched, astToPsSpecVisitor, ctx);
                sql.append(" ").append(actionSpec.sql());
                params.addAll(actionSpec.parameters());
            } else if (action instanceof WhenNotMatchedInsert) {
                // Already handled in SELECT clause
            } else if (action instanceof WhenMatchedDelete whenMatchedDelete) {
                PreparedStatementSpec actionSpec =
                        whenMatchedDeleteStrategy.handle(whenMatchedDelete, astToPsSpecVisitor, ctx);
                sql.append(" ").append(actionSpec.sql());
                params.addAll(actionSpec.parameters());
            } else {
                throw new IllegalArgumentException("Unknown merge action type: " + action.getClass());
            }
        }
    }
}
