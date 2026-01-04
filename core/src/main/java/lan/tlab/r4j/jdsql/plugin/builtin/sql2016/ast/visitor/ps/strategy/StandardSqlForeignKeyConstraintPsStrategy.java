package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ForeignKeyConstraintPsStrategy;

public class StandardSqlForeignKeyConstraintPsStrategy implements ForeignKeyConstraintPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            ForeignKeyConstraintDefinition constraint,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx) {
        // Foreign key constraints are static DDL elements without parameters
        // Inline rendering logic from StandardSqlForeignKeyConstraintRenderStrategy
        String columns = constraint.columns().stream()
                .map(c -> astToPsSpecVisitor.getEscapeStrategy().apply(c))
                .collect(java.util.stream.Collectors.joining(", "));
        PreparedStatementSpec referencesDto = constraint.references().accept(astToPsSpecVisitor, ctx);
        String sql = String.format("FOREIGN KEY (%s) %s", columns, referencesDto.sql());
        return new PreparedStatementSpec(sql, referencesDto.parameters());
    }
}
