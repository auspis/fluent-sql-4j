package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.Mod;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ModPsStrategy;

public class StandardSqlModPsStrategy implements ModPsStrategy {

    @Override
    public PsDto handle(Mod mod, PreparedStatementRenderer renderer, AstContext ctx) {
        PsDto dividendResult = mod.dividend().accept(renderer, ctx);
        PsDto divisorResult = mod.divisor().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(dividendResult.parameters());
        parameters.addAll(divisorResult.parameters());

        String sql = String.format("MOD(%s, %s)", dividendResult.sql(), divisorResult.sql());

        return new PsDto(sql, parameters);
    }
}
