package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import io.github.auspis.fluentsql4j.ast.core.expression.function.number.Power;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.PowerPsStrategy;

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
