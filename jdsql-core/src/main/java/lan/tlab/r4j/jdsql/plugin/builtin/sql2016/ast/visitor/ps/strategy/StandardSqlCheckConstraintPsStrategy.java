package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.CheckConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CheckConstraintPsStrategy;

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
