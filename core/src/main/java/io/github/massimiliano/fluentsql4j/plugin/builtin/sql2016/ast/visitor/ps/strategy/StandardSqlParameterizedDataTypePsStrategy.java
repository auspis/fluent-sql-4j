package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.ddl.definition.DataType.ParameterizedDataType;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.ParameterizedDataTypePsStrategy;
import java.util.stream.Collectors;

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
