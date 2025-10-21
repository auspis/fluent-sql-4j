package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UpdateItemPsStrategy;

public class DefaultUpdateItemPsStrategy implements UpdateItemPsStrategy {

    @Override
    public PsDto handle(UpdateItem item, PreparedStatementRenderer renderer, AstContext ctx) {
        PsDto columnDto = item.getColumn().accept(renderer, ctx);
        PsDto valueDto = item.getValue().accept(renderer, ctx);

        String sql = String.format("%s = %s", columnDto.sql(), valueDto.sql());

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(columnDto.parameters());
        parameters.addAll(valueDto.parameters());

        return new PsDto(sql, parameters);
    }
}
