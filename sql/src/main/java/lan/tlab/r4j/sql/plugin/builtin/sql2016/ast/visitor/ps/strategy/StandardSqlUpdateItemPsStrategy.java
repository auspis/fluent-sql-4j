package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UpdateItemPsStrategy;

public class StandardSqlUpdateItemPsStrategy implements UpdateItemPsStrategy {

    @Override
    public PsDto handle(UpdateItem item, PreparedStatementRenderer renderer, AstContext ctx) {
        PsDto columnDto = item.column().accept(renderer, ctx);
        PsDto valueDto = item.value().accept(renderer, ctx);

        String sql = String.format("%s = %s", columnDto.sql(), valueDto.sql());

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(columnDto.parameters());
        parameters.addAll(valueDto.parameters());

        return new PsDto(sql, parameters);
    }
}
