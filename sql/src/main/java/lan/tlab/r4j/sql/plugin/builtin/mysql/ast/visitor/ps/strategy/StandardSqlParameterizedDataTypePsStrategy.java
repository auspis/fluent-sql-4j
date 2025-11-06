package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType.ParameterizedDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ParameterizedDataTypePsStrategy;

public class StandardSqlParameterizedDataTypePsStrategy implements ParameterizedDataTypePsStrategy {

    @Override
    public PsDto handle(ParameterizedDataType type, PreparedStatementRenderer renderer, AstContext ctx) {
        var parameterResults = type.parameters().stream()
                .map(param -> param.accept(renderer, ctx))
                .collect(Collectors.toList());

        var combinedParameters = parameterResults.stream()
                .flatMap(dto -> dto.parameters().stream())
                .collect(Collectors.toList());

        var parameterSql = parameterResults.stream().map(PsDto::sql).collect(Collectors.joining(", "));

        var sql = String.format("%s(%s)", type.name(), parameterSql);

        return new PsDto(sql, combinedParameters);
    }
}
