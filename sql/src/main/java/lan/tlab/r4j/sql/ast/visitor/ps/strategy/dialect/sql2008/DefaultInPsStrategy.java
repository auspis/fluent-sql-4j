package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.predicate.In;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.InPsStrategy;

public class DefaultInPsStrategy implements InPsStrategy {
    @Override
    public PsDto handle(In in, Visitor<PsDto> renderer, AstContext ctx) {
        PsDto expressionDto = in.getExpression().accept(renderer, ctx);

        List<PsDto> valueDtos = in.getValues().stream()
                .map(value -> value.accept(renderer, ctx))
                .collect(Collectors.toList());

        String valuesSql = valueDtos.stream().map(PsDto::sql).collect(Collectors.joining(", "));

        String sql = expressionDto.sql() + " IN (" + valuesSql + ")";

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(expressionDto.parameters());
        valueDtos.forEach(dto -> parameters.addAll(dto.parameters()));

        return new PsDto(sql, parameters);
    }
}
