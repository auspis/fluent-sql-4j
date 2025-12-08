package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ColumnDefinitionPsStrategy;

public class StandardSqlColumnDefinitionPsStrategy implements ColumnDefinitionPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            ColumnDefinition columnDefinition, PreparedStatementRenderer renderer, AstContext ctx) {
        if (columnDefinition.equals(ColumnDefinition.nullObject())) {
            return new PreparedStatementSpec("", List.of());
        }

        StringBuilder builder = new StringBuilder();
        List<Object> parameters = new ArrayList<>();

        // Handle column name - use escape strategy from visitor
        String columnName = renderer.getEscapeStrategy().apply(columnDefinition.name());
        builder.append(columnName);

        // Handle data type - use PreparedStatementRenderer directly
        PreparedStatementSpec typeDto = columnDefinition.type().accept(renderer, ctx);
        builder.append(" ").append(typeDto.sql());
        parameters.addAll(typeDto.parameters());

        // Handle constraints (NOT NULL, DEFAULT) - these may have parameters
        List<PreparedStatementSpec> constraintDtos = Stream.of(
                        columnDefinition.notNullConstraint(), columnDefinition.defaultConstraint())
                .filter(c -> c != null)
                .map(c -> c.accept(renderer, ctx))
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
