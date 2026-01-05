package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CheckConstraintPsStrategy;

public class StandardSqlCheckConstraintPsStrategy implements CheckConstraintPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            CheckConstraintDefinition constraint,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx) {
        // Check constraints are static DDL elements without parameters
        // Inline rendering logic from StandardSqlCheckConstraintRenderStrategy
        PreparedStatementSpec exprDto = constraint.expression().accept(astToPsSpecVisitor, ctx);
        String sql = "CHECK (" + exprDto.sql() + ")";
        return new PreparedStatementSpec(sql, exprDto.parameters());
    }
}
