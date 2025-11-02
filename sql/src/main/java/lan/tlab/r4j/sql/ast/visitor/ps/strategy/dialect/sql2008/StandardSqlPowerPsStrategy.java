package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.Power;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.PowerPsStrategy;

public class StandardSqlPowerPsStrategy implements PowerPsStrategy {

    @Override
    public PsDto handle(Power power, PreparedStatementRenderer renderer, AstContext ctx) {
        PsDto baseResult = power.base().accept(renderer, ctx);
        PsDto exponentResult = power.exponent().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(baseResult.parameters());
        parameters.addAll(exponentResult.parameters());

        String sql = String.format("POWER(%s, %s)", baseResult.sql(), exponentResult.sql());

        return new PsDto(sql, parameters);
    }
}
