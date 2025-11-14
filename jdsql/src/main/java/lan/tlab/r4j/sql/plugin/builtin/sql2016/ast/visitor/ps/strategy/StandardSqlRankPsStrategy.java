package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.scalar.window.Rank;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.RankPsStrategy;

public class StandardSqlRankPsStrategy implements RankPsStrategy {

    @Override
    public PsDto handle(Rank rank, Visitor<PsDto> visitor, AstContext ctx) {
        StringBuilder sql = new StringBuilder("RANK()");
        List<Object> parameters = new ArrayList<>();

        if (rank.overClause() != null) {
            PsDto overResult = rank.overClause().accept(visitor, ctx);
            sql.append(" ").append(overResult.sql());
            parameters.addAll(overResult.parameters());
        }

        return new PsDto(sql.toString(), parameters);
    }
}
