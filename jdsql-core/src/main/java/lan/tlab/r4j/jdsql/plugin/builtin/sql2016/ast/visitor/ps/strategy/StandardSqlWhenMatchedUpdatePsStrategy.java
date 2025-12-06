package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.WhenMatchedUpdatePsStrategy;

public class StandardSqlWhenMatchedUpdatePsStrategy implements WhenMatchedUpdatePsStrategy {

    @Override
    public PsDto handle(WhenMatchedUpdate item, PreparedStatementRenderer visitor, AstContext ctx) {
        List<Object> allParameters = new ArrayList<>();
        StringBuilder sql = new StringBuilder("WHEN MATCHED");

        if (item.condition() != null) {
            PsDto conditionDto = item.condition().accept(visitor, ctx);
            allParameters.addAll(conditionDto.parameters());
            sql.append(" AND ").append(conditionDto.sql());
        }

        sql.append(" THEN UPDATE SET ");

        String updates = item.updateItems().stream()
                .map(updateItem -> {
                    PsDto updateDto = updateItem.accept(visitor, ctx);
                    allParameters.addAll(updateDto.parameters());
                    return updateDto.sql();
                })
                .collect(java.util.stream.Collectors.joining(", "));

        sql.append(updates);

        return new PsDto(sql.toString(), allParameters);
    }
}
