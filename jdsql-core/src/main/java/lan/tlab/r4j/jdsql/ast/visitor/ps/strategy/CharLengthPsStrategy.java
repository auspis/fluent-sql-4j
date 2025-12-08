package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.function.string.CharLength;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface CharLengthPsStrategy {
    PreparedStatementSpec handle(CharLength charLength, PreparedStatementRenderer renderer, AstContext ctx);
}
