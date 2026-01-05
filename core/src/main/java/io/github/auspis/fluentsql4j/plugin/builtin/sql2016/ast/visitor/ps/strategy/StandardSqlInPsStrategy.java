package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import io.github.auspis.fluentsql4j.ast.core.predicate.In;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.InPsStrategy;

public class StandardSqlInPsStrategy implements InPsStrategy {
    @Override
    public PreparedStatementSpec handle(In in, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        PreparedStatementSpec expressionDto = in.expression().accept(renderer, ctx);

        List<PreparedStatementSpec> valueDtos =
                in.values().stream().map(value -> value.accept(renderer, ctx)).collect(Collectors.toList());

        String valuesSql = valueDtos.stream().map(PreparedStatementSpec::sql).collect(Collectors.joining(", "));

        String sql = expressionDto.sql() + " IN (" + valuesSql + ")";

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(expressionDto.parameters());
        valueDtos.forEach(dto -> parameters.addAll(dto.parameters()));

        return new PreparedStatementSpec(sql, parameters);
    }
}
