package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.Power;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.PowerPsStrategy;

public class DefaultPowerPsStrategy implements PowerPsStrategy {

    @Override
    public PsDto handle(Power power, PreparedStatementVisitor visitor, AstContext ctx) {
        PsDto baseResult = power.getBase().accept(visitor, ctx);
        PsDto exponentResult = power.getExponent().accept(visitor, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(baseResult.parameters());
        parameters.addAll(exponentResult.parameters());

        String sql = String.format("POWER(%s, %s)", baseResult.sql(), exponentResult.sql());

        return new PsDto(sql, parameters);
    }
}
