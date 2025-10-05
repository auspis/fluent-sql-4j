package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Replace;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ReplacePsStrategy;

public class DefaultReplacePsStrategy implements ReplacePsStrategy {

    @Override
    public PsDto handle(Replace replace, PreparedStatementVisitor visitor, AstContext ctx) {
        PsDto expressionResult = replace.getExpression().accept(visitor, ctx);
        PsDto oldSubstringResult = replace.getOldSubstring().accept(visitor, ctx);
        PsDto newSubstringResult = replace.getNewSubstring().accept(visitor, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(expressionResult.parameters());
        parameters.addAll(oldSubstringResult.parameters());
        parameters.addAll(newSubstringResult.parameters());

        String sql = String.format(
                "REPLACE(%s, %s, %s)", expressionResult.sql(), oldSubstringResult.sql(), newSubstringResult.sql());

        return new PsDto(sql, parameters);
    }
}
