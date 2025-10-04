package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.predicate.Between;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.BetweenPsStrategy;

public class DefaultBetweenPsStrategy implements BetweenPsStrategy {
    @Override
    public PsDto handle(Between between, Visitor<PsDto> visitor, AstContext ctx) {
        PsDto testDto = between.getTestExpression().accept(visitor, ctx);
        PsDto startDto = between.getStartExpression().accept(visitor, ctx);
        PsDto endDto = between.getEndExpression().accept(visitor, ctx);

        String sql = testDto.sql() + " BETWEEN " + startDto.sql() + " AND " + endDto.sql();

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(testDto.parameters());
        parameters.addAll(startDto.parameters());
        parameters.addAll(endDto.parameters());

        return new PsDto(sql, parameters);
    }
}
