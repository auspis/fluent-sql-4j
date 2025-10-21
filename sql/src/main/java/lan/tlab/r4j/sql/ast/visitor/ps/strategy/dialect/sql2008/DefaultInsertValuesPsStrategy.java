package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertValues;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.InsertValuesPsStrategy;

public class DefaultInsertValuesPsStrategy implements InsertValuesPsStrategy {
    @Override
    public PsDto handle(InsertValues insertValues, Visitor<PsDto> renderer, AstContext ctx) {
        List<String> placeholders = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (var expr : insertValues.getValueExpressions()) {
            if (expr instanceof Literal<?> literal) {
                placeholders.add("?");
                params.add(literal.getValue());
            } else {
                // Fallback for non-literal expressions
                placeholders.add("?");
                params.add(null);
            }
        }
        String sql = String.join(", ", placeholders);
        return new PsDto(sql, params);
    }
}
