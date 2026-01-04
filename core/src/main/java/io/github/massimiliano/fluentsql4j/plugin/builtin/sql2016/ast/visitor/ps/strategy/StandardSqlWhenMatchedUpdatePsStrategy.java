package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedUpdate;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.WhenMatchedUpdatePsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlWhenMatchedUpdatePsStrategy implements WhenMatchedUpdatePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            WhenMatchedUpdate item, AstToPreparedStatementSpecVisitor visitor, AstContext ctx) {
        List<Object> allParameters = new ArrayList<>();
        StringBuilder sql = new StringBuilder("WHEN MATCHED");

        if (item.condition() != null) {
            PreparedStatementSpec conditionDto = item.condition().accept(visitor, ctx);
            allParameters.addAll(conditionDto.parameters());
            sql.append(" AND ").append(conditionDto.sql());
        }

        sql.append(" THEN UPDATE SET ");

        String updates = item.updateItems().stream()
                .map(updateItem -> {
                    PreparedStatementSpec updateDto = updateItem.accept(visitor, ctx);
                    allParameters.addAll(updateDto.parameters());
                    return updateDto.sql();
                })
                .collect(java.util.stream.Collectors.joining(", "));

        sql.append(updates);

        return new PreparedStatementSpec(sql.toString(), allParameters);
    }
}
