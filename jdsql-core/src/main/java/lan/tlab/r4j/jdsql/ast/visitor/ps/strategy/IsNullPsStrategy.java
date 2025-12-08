package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.predicate.IsNull;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface IsNullPsStrategy {
    PreparedStatementSpec handle(IsNull isNull, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
