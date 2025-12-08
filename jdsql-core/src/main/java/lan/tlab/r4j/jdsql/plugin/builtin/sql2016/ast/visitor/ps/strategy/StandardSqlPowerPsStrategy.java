package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number.Power;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.PowerPsStrategy;

public class StandardSqlPowerPsStrategy implements PowerPsStrategy {

    @Override
    public PreparedStatementSpec handle(Power power, PreparedStatementRenderer renderer, AstContext ctx) {
        PreparedStatementSpec baseResult = power.base().accept(renderer, ctx);
        PreparedStatementSpec exponentResult = power.exponent().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(baseResult.parameters());
        parameters.addAll(exponentResult.parameters());

        String sql = String.format("POWER(%s, %s)", baseResult.sql(), exponentResult.sql());

        return new PreparedStatementSpec(sql, parameters);
    }
}
