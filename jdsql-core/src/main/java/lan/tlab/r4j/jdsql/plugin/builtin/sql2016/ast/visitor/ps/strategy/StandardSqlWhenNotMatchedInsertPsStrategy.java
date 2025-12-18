package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.WhenNotMatchedInsertPsStrategy;

public class StandardSqlWhenNotMatchedInsertPsStrategy implements WhenNotMatchedInsertPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            WhenNotMatchedInsert item, AstToPreparedStatementSpecVisitor visitor, AstContext ctx) {
        List<Object> allParameters = new ArrayList<>();
        StringBuilder sql = new StringBuilder("WHEN NOT MATCHED");

        if (item.condition() != null) {
            PreparedStatementSpec conditionDto = item.condition().accept(visitor, ctx);
            allParameters.addAll(conditionDto.parameters());
            sql.append(" AND ").append(conditionDto.sql());
        }

        sql.append(" THEN INSERT");

        if (!item.columns().isEmpty()) {
            String columns = item.columns().stream()
                    .map(col -> {
                        PreparedStatementSpec colDto = col.accept(visitor, ctx);
                        allParameters.addAll(colDto.parameters());
                        return colDto.sql();
                    })
                    .collect(java.util.stream.Collectors.joining(", "));
            sql.append(" (").append(columns).append(")");
        }

        PreparedStatementSpec insertDataDto = item.insertData().accept(visitor, ctx);
        allParameters.addAll(insertDataDto.parameters());
        sql.append(" ").append(insertDataDto.sql());

        return new PreparedStatementSpec(sql.toString(), allParameters);
    }
}
