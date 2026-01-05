package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import io.github.auspis.fluentsql4j.ast.core.expression.function.string.Concat;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ConcatPsStrategy;

public class StandardSqlConcatPsStrategy implements ConcatPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            Concat concat, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        List<Object> allParameters = new ArrayList<>();

        // Visit all expressions to get their SQL and parameters
        List<String> sqlExpressions = concat.stringExpressions().stream()
                .map(expr -> {
                    PreparedStatementSpec exprDto = expr.accept(astToPsSpecVisitor, ctx);
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
