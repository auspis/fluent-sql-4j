package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.expression.item.ddl.Constraint.PrimaryKey;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface PrimaryKeyPsStrategy {
    PsDto handle(PrimaryKey item, PreparedStatementVisitor visitor, AstContext ctx);
}
