package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import io.github.auspis.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.UpdateItemPsStrategy;

public class StandardSqlUpdateItemPsStrategy implements UpdateItemPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            UpdateItem item, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        PreparedStatementSpec columnDto = item.column().accept(astToPsSpecVisitor, ctx);
        PreparedStatementSpec valueDto = item.value().accept(astToPsSpecVisitor, ctx);

        String sql = String.format("%s = %s", columnDto.sql(), valueDto.sql());

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(columnDto.parameters());
        parameters.addAll(valueDto.parameters());

        return new PreparedStatementSpec(sql, parameters);
    }
}
