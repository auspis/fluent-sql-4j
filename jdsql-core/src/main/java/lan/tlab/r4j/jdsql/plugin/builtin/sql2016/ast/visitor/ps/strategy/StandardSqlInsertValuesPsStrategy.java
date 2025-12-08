package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.InsertValues;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.InsertValuesPsStrategy;

public class StandardSqlInsertValuesPsStrategy implements InsertValuesPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            InsertValues insertValues, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        List<String> placeholders = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (var expr : insertValues.valueExpressions()) {
            if (expr instanceof Literal<?> literal) {
                placeholders.add("?");
                params.add(literal.value());
            } else {
                // Fallback for non-literal expressions
                placeholders.add("?");
                params.add(null);
            }
        }
        String sql = String.join(", ", placeholders);
        return new PreparedStatementSpec(sql, params);
    }
}
