package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import io.github.auspis.fluentsql4j.ast.core.expression.function.string.Left;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.LeftPsStrategy;

public class StandardSqlLeftPsStrategy implements LeftPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            Left left, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        var expressionResult = left.expression().accept(astToPsSpecVisitor, ctx);
        var lengthResult = left.length().accept(astToPsSpecVisitor, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(expressionResult.parameters());
        parameters.addAll(lengthResult.parameters());

        String sql = String.format("LEFT(%s, %s)", expressionResult.sql(), lengthResult.sql());
        return new PreparedStatementSpec(sql, parameters);
    }
}
