package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.ExtractDatePart;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public interface ExtractDatePartPsStrategy {
    PsDto handle(ExtractDatePart extractDatePart, PreparedStatementVisitor visitor, AstContext ctx);
}
