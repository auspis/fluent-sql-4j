package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.Trim;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

public interface TrimPsStrategy {

    PsDto handle(Trim functionCall, PreparedStatementRenderer renderer, AstContext ctx);
}
