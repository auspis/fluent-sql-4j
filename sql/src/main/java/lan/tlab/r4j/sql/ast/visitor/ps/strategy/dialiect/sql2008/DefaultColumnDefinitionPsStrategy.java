package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.expression.item.ddl.ColumnDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ColumnDefinitionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;

public class DefaultColumnDefinitionPsStrategy implements ColumnDefinitionPsStrategy {

    @Override
    public PsDto handle(ColumnDefinition columnDefinition, PreparedStatementVisitor visitor, AstContext ctx) {
        if (columnDefinition.equals(ColumnDefinition.nullObject())) {
            return new PsDto("", List.of());
        }

        StringBuilder builder = new StringBuilder();
        List<Object> parameters = new ArrayList<>();

        // Handle column name - use escape strategy from visitor
        String columnName = visitor.getEscapeStrategy().apply(columnDefinition.getName());
        builder.append(columnName);

        // Handle data type using SQL renderer since data types are static DDL elements
        String typeString = columnDefinition.getType().accept(SqlRendererFactory.standardSql2008(), ctx);
        builder.append(" ").append(typeString);

        // Handle constraints (NOT NULL, DEFAULT) - these may have parameters
        List<PsDto> constraintDtos = Stream.of(
                        columnDefinition.getNotNullConstraint(), columnDefinition.getDefaultConstraint())
                .filter(c -> c != null)
                .map(c -> c.accept(visitor, ctx))
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
