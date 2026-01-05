package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.ddl.definition.ColumnDefinition;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ColumnDefinitionPsStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StandardSqlColumnDefinitionPsStrategy implements ColumnDefinitionPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            ColumnDefinition columnDefinition, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        if (columnDefinition.equals(ColumnDefinition.nullObject())) {
            return new PreparedStatementSpec("", List.of());
        }

        StringBuilder builder = new StringBuilder();
        List<Object> parameters = new ArrayList<>();

        // Handle column name - use escape strategy from visitor
        String columnName = astToPsSpecVisitor.getEscapeStrategy().apply(columnDefinition.name());
        builder.append(columnName);

        // Handle data type - use AstToPreparedStatementSpecVisitor directly
        PreparedStatementSpec typeDto = columnDefinition.type().accept(astToPsSpecVisitor, ctx);
        builder.append(" ").append(typeDto.sql());
        parameters.addAll(typeDto.parameters());

        // Handle constraints (NOT NULL, DEFAULT) - these may have parameters
        List<PreparedStatementSpec> constraintDtos = Stream.of(
                        columnDefinition.notNullConstraint(), columnDefinition.defaultConstraint())
                .filter(c -> c != null)
                .map(c -> c.accept(astToPsSpecVisitor, ctx))
                .collect(Collectors.toList());

        if (!constraintDtos.isEmpty()) {
            String constraintsSql =
                    constraintDtos.stream().map(PreparedStatementSpec::sql).collect(Collectors.joining(" "));

            builder.append(" ").append(constraintsSql);

            for (PreparedStatementSpec constraintDto : constraintDtos) {
                parameters.addAll(constraintDto.parameters());
            }
        }

        return new PreparedStatementSpec(builder.toString().trim(), parameters);
    }
}
