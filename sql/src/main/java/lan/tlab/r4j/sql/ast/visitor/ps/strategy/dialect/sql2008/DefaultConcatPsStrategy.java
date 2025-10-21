package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Concat;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ConcatPsStrategy;

public class DefaultConcatPsStrategy implements ConcatPsStrategy {

    @Override
    public PsDto handle(Concat concat, PreparedStatementRenderer renderer, AstContext ctx) {
        List<Object> allParameters = new ArrayList<>();

        // Visit all expressions to get their SQL and parameters
        List<String> sqlExpressions = concat.getStringExpressions().stream()
                .map(expr -> {
                    PsDto exprDto = expr.accept(renderer, ctx);
                    allParameters.addAll(exprDto.parameters());
                    return exprDto.sql();
                })
                .collect(Collectors.toList());

        // If there's a separator, we need to insert it between expressions
        String sql;
        if (concat.getSeparator().isEmpty()) {
            // Standard CONCAT function
            sql = "CONCAT(" + String.join(", ", sqlExpressions) + ")";
        } else {
            // CONCAT_WS function for concatenation with separator
            // Add separator as first parameter
            allParameters.add(0, concat.getSeparator());
            sql = "CONCAT_WS(?, " + String.join(", ", sqlExpressions) + ")";
        }

        return new PsDto(sql, allParameters);
    }
}
