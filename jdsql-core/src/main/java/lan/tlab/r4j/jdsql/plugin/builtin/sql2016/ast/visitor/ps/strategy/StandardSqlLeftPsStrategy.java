package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.Left;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.LeftPsStrategy;

public class StandardSqlLeftPsStrategy implements LeftPsStrategy {

    @Override
    public PreparedStatementSpec handle(Left left, AstToPreparedStatementSpecVisitor renderer, AstContext ctx) {
        var expressionResult = left.expression().accept(renderer, ctx);
        var lengthResult = left.length().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(expressionResult.parameters());
        parameters.addAll(lengthResult.parameters());

        String sql = String.format("LEFT(%s, %s)", expressionResult.sql(), lengthResult.sql());
        return new PreparedStatementSpec(sql, parameters);
    }
}
