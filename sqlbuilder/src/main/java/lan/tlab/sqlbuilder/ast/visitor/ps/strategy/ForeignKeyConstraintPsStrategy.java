package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.ForeignKeyConstraint;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public interface ForeignKeyConstraintPsStrategy {
    PsDto handle(ForeignKeyConstraint constraint, PreparedStatementVisitor visitor, AstContext ctx);
}
