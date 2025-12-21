package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.Replace;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ReplacePsStrategy;

public class StandardSqlReplacePsStrategy implements ReplacePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            Replace replace, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        PreparedStatementSpec expressionResult = replace.expression().accept(astToPsSpecVisitor, ctx);
        PreparedStatementSpec oldSubstringResult = replace.oldSubstring().accept(astToPsSpecVisitor, ctx);
        PreparedStatementSpec newSubstringResult = replace.newSubstring().accept(astToPsSpecVisitor, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(expressionResult.parameters());
        parameters.addAll(oldSubstringResult.parameters());
        parameters.addAll(newSubstringResult.parameters());

        String sql = String.format(
                "REPLACE(%s, %s, %s)", expressionResult.sql(), oldSubstringResult.sql(), newSubstringResult.sql());

        return new PreparedStatementSpec(sql, parameters);
    }
}
