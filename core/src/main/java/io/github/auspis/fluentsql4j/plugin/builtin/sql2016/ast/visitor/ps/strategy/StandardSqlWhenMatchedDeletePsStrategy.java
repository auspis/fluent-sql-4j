package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedDelete;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.WhenMatchedDeletePsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlWhenMatchedDeletePsStrategy implements WhenMatchedDeletePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            WhenMatchedDelete item, AstToPreparedStatementSpecVisitor visitor, AstContext ctx) {
        List<Object> allParameters = new ArrayList<>();
        StringBuilder sql = new StringBuilder("WHEN MATCHED");

        if (item.condition() != null) {
            PreparedStatementSpec conditionDto = item.condition().accept(visitor, ctx);
            allParameters.addAll(conditionDto.parameters());
            sql.append(" AND ").append(conditionDto.sql());
        }

        sql.append(" THEN DELETE");

        return new PreparedStatementSpec(sql.toString(), allParameters);
    }
}
