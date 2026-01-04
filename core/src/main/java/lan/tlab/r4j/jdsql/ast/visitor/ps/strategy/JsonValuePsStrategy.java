package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.function.json.JsonValue;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface JsonValuePsStrategy {
    PreparedStatementSpec handle(
            JsonValue jsonValue, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
