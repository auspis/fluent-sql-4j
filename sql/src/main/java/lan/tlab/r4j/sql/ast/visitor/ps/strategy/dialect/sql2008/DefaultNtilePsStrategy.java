package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Ntile;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NtilePsStrategy;

public class DefaultNtilePsStrategy implements NtilePsStrategy {

    @Override
    public PsDto handle(Ntile ntile, Visitor<PsDto> visitor, AstContext ctx) {
        StringBuilder sql =
                new StringBuilder("NTILE(").append(ntile.getBuckets()).append(")");
        List<Object> parameters = new ArrayList<>();

        if (ntile.getOverClause() != null) {
            PsDto overResult = ntile.getOverClause().accept(visitor, ctx);
            sql.append(" ").append(overResult.sql());
            parameters.addAll(overResult.parameters());
        }

        return new PsDto(sql.toString(), parameters);
    }
}
