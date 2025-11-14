package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.UnaryString;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface UnaryStringPsStrategy {

    PsDto handle(UnaryString functionCall, PreparedStatementRenderer renderer, AstContext ctx);
}
