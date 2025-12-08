package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number.Mod;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ModPsStrategy;

public class StandardSqlModPsStrategy implements ModPsStrategy {

    @Override
    public PreparedStatementSpec handle(Mod mod, PreparedStatementRenderer renderer, AstContext ctx) {
        PreparedStatementSpec dividendResult = mod.dividend().accept(renderer, ctx);
        PreparedStatementSpec divisorResult = mod.divisor().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(dividendResult.parameters());
        parameters.addAll(divisorResult.parameters());

        String sql = String.format("MOD(%s, %s)", dividendResult.sql(), divisorResult.sql());

        return new PreparedStatementSpec(sql, parameters);
    }
}
