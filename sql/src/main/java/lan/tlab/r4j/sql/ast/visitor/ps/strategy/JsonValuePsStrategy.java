package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonValue;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface JsonValuePsStrategy {
    PsDto handle(JsonValue jsonValue, PreparedStatementRenderer renderer, AstContext ctx);
}
