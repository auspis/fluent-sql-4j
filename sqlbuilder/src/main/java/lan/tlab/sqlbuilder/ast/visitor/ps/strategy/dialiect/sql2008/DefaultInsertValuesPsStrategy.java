package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertValues;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.InsertValuesPsStrategy;

public class DefaultInsertValuesPsStrategy implements InsertValuesPsStrategy {
    @Override
    public PsDto handle(InsertValues insertValues, Visitor<PsDto> visitor, AstContext ctx) {
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
