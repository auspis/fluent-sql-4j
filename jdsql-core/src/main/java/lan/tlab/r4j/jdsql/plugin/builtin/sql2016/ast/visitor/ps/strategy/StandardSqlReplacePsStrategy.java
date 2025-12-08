package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Replace;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ReplacePsStrategy;

public class StandardSqlReplacePsStrategy implements ReplacePsStrategy {

    @Override
    public PreparedStatementSpec handle(Replace replace, PreparedStatementRenderer renderer, AstContext ctx) {
        PreparedStatementSpec expressionResult = replace.expression().accept(renderer, ctx);
        PreparedStatementSpec oldSubstringResult = replace.oldSubstring().accept(renderer, ctx);
        PreparedStatementSpec newSubstringResult = replace.newSubstring().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(expressionResult.parameters());
        parameters.addAll(oldSubstringResult.parameters());
        parameters.addAll(newSubstringResult.parameters());

        String sql = String.format(
                "REPLACE(%s, %s, %s)", expressionResult.sql(), oldSubstringResult.sql(), newSubstringResult.sql());

        return new PreparedStatementSpec(sql, parameters);
    }
}
