package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.Ntile;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.NtilePsStrategy;

public class StandardSqlNtilePsStrategy implements NtilePsStrategy {

    @Override
    public PsDto handle(Ntile ntile, Visitor<PsDto> visitor, AstContext ctx) {
        StringBuilder sql = new StringBuilder("NTILE(").append(ntile.buckets()).append(")");
        List<Object> parameters = new ArrayList<>();

        if (ntile.overClause() != null) {
            PsDto overResult = ntile.overClause().accept(visitor, ctx);
            sql.append(" ").append(overResult.sql());
            parameters.addAll(overResult.parameters());
        }

        return new PsDto(sql.toString(), parameters);
    }
}
