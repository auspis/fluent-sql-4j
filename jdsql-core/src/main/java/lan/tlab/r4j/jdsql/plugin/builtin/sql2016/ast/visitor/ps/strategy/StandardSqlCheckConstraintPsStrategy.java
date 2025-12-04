package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CheckConstraintPsStrategy;

public class StandardSqlCheckConstraintPsStrategy implements CheckConstraintPsStrategy {

    @Override
    public PsDto handle(CheckConstraintDefinition constraint, PreparedStatementRenderer renderer, AstContext ctx) {
        // Check constraints are static DDL elements without parameters
        // Inline rendering logic from StandardSqlCheckConstraintRenderStrategy
        PsDto exprDto = constraint.expression().accept(renderer, ctx);
        String sql = "CHECK (" + exprDto.sql() + ")";
        return new PsDto(sql, exprDto.parameters());
    }
}
