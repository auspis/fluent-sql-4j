package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.stream.Collectors;
import lan.tlab.r4j.jdsql.ast.ddl.definition.DataType.ParameterizedDataType;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ParameterizedDataTypePsStrategy;

public class StandardSqlParameterizedDataTypePsStrategy implements ParameterizedDataTypePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            ParameterizedDataType type, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        var parameterResults = type.parameters().stream()
                .map(param -> param.accept(astToPsSpecVisitor, ctx))
                .collect(Collectors.toList());

        var combinedParameters = parameterResults.stream()
                .flatMap(dto -> dto.parameters().stream())
                .collect(Collectors.toList());

        var parameterSql =
                parameterResults.stream().map(PreparedStatementSpec::sql).collect(Collectors.joining(", "));

        var sql = String.format("%s(%s)", type.name(), parameterSql);

        return new PreparedStatementSpec(sql, combinedParameters);
    }
}
