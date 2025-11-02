package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ColumnDefinitionPsStrategy;

public class StandardSqlColumnDefinitionPsStrategy implements ColumnDefinitionPsStrategy {

    @Override
    public PsDto handle(ColumnDefinition columnDefinition, PreparedStatementRenderer renderer, AstContext ctx) {
        if (columnDefinition.equals(ColumnDefinition.nullObject())) {
            return new PsDto("", List.of());
        }

        StringBuilder builder = new StringBuilder();
        List<Object> parameters = new ArrayList<>();

        // Handle column name - use escape strategy from visitor
        String columnName = renderer.getEscapeStrategy().apply(columnDefinition.name());
        builder.append(columnName);

        // Handle data type using the SQL renderer from the PreparedStatementRenderer
        // This ensures we use the same dialect configuration
        String typeString = columnDefinition.type().accept(renderer.getSqlRenderer(), ctx);
        builder.append(" ").append(typeString);

        // Handle constraints (NOT NULL, DEFAULT) - these may have parameters
        List<PsDto> constraintDtos = Stream.of(
                        columnDefinition.notNullConstraint(), columnDefinition.defaultConstraint())
                .filter(c -> c != null)
                .map(c -> c.accept(renderer, ctx))
                .collect(Collectors.toList());

        if (!constraintDtos.isEmpty()) {
            String constraintsSql = constraintDtos.stream().map(PsDto::sql).collect(Collectors.joining(" "));

            builder.append(" ").append(constraintsSql);

            for (PsDto constraintDto : constraintDtos) {
                parameters.addAll(constraintDto.parameters());
            }
        }

        return new PsDto(builder.toString().trim(), parameters);
    }
}
