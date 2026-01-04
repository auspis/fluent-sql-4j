package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.number.Power;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.PowerPsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlPowerPsStrategy implements PowerPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            Power power, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        PreparedStatementSpec baseResult = power.base().accept(astToPsSpecVisitor, ctx);
        PreparedStatementSpec exponentResult = power.exponent().accept(astToPsSpecVisitor, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(baseResult.parameters());
        parameters.addAll(exponentResult.parameters());

        String sql = String.format("POWER(%s, %s)", baseResult.sql(), exponentResult.sql());

        return new PreparedStatementSpec(sql, parameters);
    }
}
