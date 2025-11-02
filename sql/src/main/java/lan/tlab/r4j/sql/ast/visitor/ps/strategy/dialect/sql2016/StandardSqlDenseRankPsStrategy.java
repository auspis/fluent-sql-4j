package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.DenseRank;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DenseRankPsStrategy;

public class StandardSqlDenseRankPsStrategy implements DenseRankPsStrategy {

    @Override
    public PsDto handle(DenseRank denseRank, Visitor<PsDto> visitor, AstContext ctx) {
        StringBuilder sql = new StringBuilder("DENSE_RANK()");
        List<Object> parameters = new ArrayList<>();

        if (denseRank.overClause() != null) {
            PsDto overResult = denseRank.overClause().accept(visitor, ctx);
            sql.append(" ").append(overResult.sql());
            parameters.addAll(overResult.parameters());
        }

        return new PsDto(sql.toString(), parameters);
    }
}
