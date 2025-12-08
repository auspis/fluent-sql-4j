package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenMatchedDelete;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.WhenMatchedDeletePsStrategy;

public class StandardSqlWhenMatchedDeletePsStrategy implements WhenMatchedDeletePsStrategy {

    @Override
    public PreparedStatementSpec handle(WhenMatchedDelete item, PreparedStatementRenderer visitor, AstContext ctx) {
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
