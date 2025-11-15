package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.predicate.Between;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.BetweenPsStrategy;

public class StandardSqlBetweenPsStrategy implements BetweenPsStrategy {
    @Override
    public PsDto handle(Between between, Visitor<PsDto> renderer, AstContext ctx) {
        PsDto testDto = between.testExpression().accept(renderer, ctx);
        PsDto startDto = between.startExpression().accept(renderer, ctx);
        PsDto endDto = between.endExpression().accept(renderer, ctx);

        String sql = testDto.sql() + " BETWEEN " + startDto.sql() + " AND " + endDto.sql();

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(testDto.parameters());
        parameters.addAll(startDto.parameters());
        parameters.addAll(endDto.parameters());

        return new PsDto(sql, parameters);
    }
}
