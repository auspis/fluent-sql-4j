package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.expression.item.ddl.DataType.ParameterizedDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ParameterizedDataTypePsStrategy;

public class DefaultParameterizedDataTypePsStrategy implements ParameterizedDataTypePsStrategy {

    @Override
    public PsDto handle(ParameterizedDataType type, PreparedStatementVisitor visitor, AstContext ctx) {
        // Handle parameters by processing each one
        var parameterResults = type.getParameters().stream()
                .map(param -> param.accept(visitor, ctx))
                .collect(Collectors.toList());

        // Combine all parameters
        var combinedParameters = parameterResults.stream()
                .flatMap(dto -> dto.parameters().stream())
                .collect(Collectors.toList());

        var parameterSql = parameterResults.stream().map(PsDto::sql).collect(Collectors.joining(", "));

        var sql = String.format("%s(%s)", type.getName(), parameterSql);

        return new PsDto(sql, combinedParameters);
    }
}
