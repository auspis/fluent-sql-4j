package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Rank;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.RankPsStrategy;

public class DefaultRankPsStrategy implements RankPsStrategy {

    @Override
    public PsDto handle(Rank rank, Visitor<PsDto> visitor, AstContext ctx) {
        StringBuilder sql = new StringBuilder("RANK()");
        List<Object> parameters = new ArrayList<>();

        if (rank.getOverClause() != null) {
            PsDto overResult = rank.getOverClause().accept(visitor, ctx);
            sql.append(" ").append(overResult.sql());
            parameters.addAll(overResult.parameters());
        }

        return new PsDto(sql.toString(), parameters);
    }
}
