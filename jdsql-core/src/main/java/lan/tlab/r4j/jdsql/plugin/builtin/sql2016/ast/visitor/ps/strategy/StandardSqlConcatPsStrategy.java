package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.Concat;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ConcatPsStrategy;

public class StandardSqlConcatPsStrategy implements ConcatPsStrategy {

    @Override
    public PreparedStatementSpec handle(Concat concat, AstToPreparedStatementSpecVisitor renderer, AstContext ctx) {
        List<Object> allParameters = new ArrayList<>();

        // Visit all expressions to get their SQL and parameters
        List<String> sqlExpressions = concat.stringExpressions().stream()
                .map(expr -> {
                    PreparedStatementSpec exprDto = expr.accept(renderer, ctx);
                    allParameters.addAll(exprDto.parameters());
                    return exprDto.sql();
                })
                .collect(Collectors.toList());

        // If there's a separator, we need to insert it between expressions
        String sql;
        if (concat.separator().isEmpty()) {
            // Standard CONCAT function
            sql = "CONCAT(" + String.join(", ", sqlExpressions) + ")";
        } else {
            // CONCAT_WS function for concatenation with separator
            // Add separator as first parameter
            allParameters.add(0, concat.separator());
            sql = "CONCAT_WS(?, " + String.join(", ", sqlExpressions) + ")";
        }

        return new PreparedStatementSpec(sql, allParameters);
    }
}
