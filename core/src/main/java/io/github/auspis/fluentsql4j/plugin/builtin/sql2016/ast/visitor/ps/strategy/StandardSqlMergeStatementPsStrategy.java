package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

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

public class StandardSqlMergeStatementPsStrategy implements MergeStatementPsStrategy {

    private final WhenMatchedUpdatePsStrategy whenMatchedUpdateStrategy;
    private final WhenNotMatchedInsertPsStrategy whenNotMatchedInsertStrategy;
    private final WhenMatchedDeletePsStrategy whenMatchedDeleteStrategy;

    public StandardSqlMergeStatementPsStrategy() {
        this.whenMatchedUpdateStrategy = new StandardSqlWhenMatchedUpdatePsStrategy();
        this.whenNotMatchedInsertStrategy = new StandardSqlWhenNotMatchedInsertPsStrategy();
        this.whenMatchedDeleteStrategy = new StandardSqlWhenMatchedDeletePsStrategy();
    }

    @Override
    public PreparedStatementSpec handle(
            MergeStatement stmt, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        // MERGE INTO target
        PreparedStatementSpec targetDto = stmt.getTargetTable().accept(astToPsSpecVisitor, ctx);
        sql.append("MERGE INTO ").append(targetDto.sql());
        params.addAll(targetDto.parameters());

        // USING source
        PreparedStatementSpec usingDto = stmt.getUsing().accept(astToPsSpecVisitor, ctx);
        sql.append(" USING ").append(usingDto.sql());
        params.addAll(usingDto.parameters());

        // ON condition - use JOIN_ON scope to qualify column references
        AstContext onCtx = new AstContext(AstContext.Feature.JOIN_ON);
        PreparedStatementSpec onDto = stmt.getOnCondition().accept(astToPsSpecVisitor, onCtx);
        sql.append(" ON ").append(onDto.sql());
        params.addAll(onDto.parameters());

        // WHEN MATCHED / WHEN NOT MATCHED actions - delegate to individual strategies
        for (MergeAction action : stmt.getActions()) {
            if (action instanceof WhenMatchedUpdate whenMatched) {
                PreparedStatementSpec actionSpec =
                        whenMatchedUpdateStrategy.handle(whenMatched, astToPsSpecVisitor, onCtx);
                sql.append(" ").append(actionSpec.sql());
                params.addAll(actionSpec.parameters());
            } else if (action instanceof WhenNotMatchedInsert whenNotMatched) {
                PreparedStatementSpec actionSpec =
                        whenNotMatchedInsertStrategy.handle(whenNotMatched, astToPsSpecVisitor, onCtx);
                sql.append(" ").append(actionSpec.sql());
                params.addAll(actionSpec.parameters());
            } else if (action instanceof WhenMatchedDelete whenMatchedDelete) {
                PreparedStatementSpec actionSpec =
                        whenMatchedDeleteStrategy.handle(whenMatchedDelete, astToPsSpecVisitor, onCtx);
                sql.append(" ").append(actionSpec.sql());
                params.addAll(actionSpec.parameters());
            } else {
                // Unknown action type - shouldn't happen
                throw new IllegalArgumentException("Unknown merge action type: " + action.getClass());
            }
        }

        return new PreparedStatementSpec(sql.toString(), params);
    }
}
