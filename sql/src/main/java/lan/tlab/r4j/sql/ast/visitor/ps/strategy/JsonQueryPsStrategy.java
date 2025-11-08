package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.JsonQuery;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface JsonQueryPsStrategy {
    PsDto handle(JsonQuery jsonQuery, PreparedStatementRenderer renderer, AstContext ctx);
}
