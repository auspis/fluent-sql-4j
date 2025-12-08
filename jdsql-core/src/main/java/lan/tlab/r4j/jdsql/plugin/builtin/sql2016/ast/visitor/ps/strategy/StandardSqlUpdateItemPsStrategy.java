package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UpdateItemPsStrategy;

public class StandardSqlUpdateItemPsStrategy implements UpdateItemPsStrategy {

    @Override
    public PreparedStatementSpec handle(UpdateItem item, PreparedStatementRenderer renderer, AstContext ctx) {
        PreparedStatementSpec columnDto = item.column().accept(renderer, ctx);
        PreparedStatementSpec valueDto = item.value().accept(renderer, ctx);

        String sql = String.format("%s = %s", columnDto.sql(), valueDto.sql());

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(columnDto.parameters());
        parameters.addAll(valueDto.parameters());

        return new PreparedStatementSpec(sql, parameters);
    }
}
