package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ForeignKeyConstraintPsStrategy;

public class StandardSqlForeignKeyConstraintPsStrategy implements ForeignKeyConstraintPsStrategy {

    @Override
    public PsDto handle(ForeignKeyConstraintDefinition constraint, PreparedStatementRenderer renderer, AstContext ctx) {
        // Foreign key constraints are static DDL elements without parameters
        // Inline rendering logic from StandardSqlForeignKeyConstraintRenderStrategy
        String columns = constraint.columns().stream()
                .map(c -> renderer.getEscapeStrategy().apply(c))
                .collect(java.util.stream.Collectors.joining(", "));
        PsDto referencesDto = constraint.references().accept(renderer, ctx);
        String sql = String.format("FOREIGN KEY (%s) %s", columns, referencesDto.sql());
        return new PsDto(sql, referencesDto.parameters());
    }
}
