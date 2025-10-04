package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.Mod;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ModPsStrategy;

public class DefaultModPsStrategy implements ModPsStrategy {

    @Override
    public PsDto handle(Mod mod, PreparedStatementVisitor visitor, AstContext ctx) {
        PsDto dividendResult = mod.getDividend().accept(visitor, ctx);
        PsDto divisorResult = mod.getDivisor().accept(visitor, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(dividendResult.parameters());
        parameters.addAll(divisorResult.parameters());

        String sql = String.format("MOD(%s, %s)", dividendResult.sql(), divisorResult.sql());

        return new PsDto(sql, parameters);
    }
}
