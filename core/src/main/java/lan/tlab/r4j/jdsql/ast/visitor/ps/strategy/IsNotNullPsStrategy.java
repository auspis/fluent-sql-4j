package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.predicate.IsNotNull;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface IsNotNullPsStrategy {
    PreparedStatementSpec handle(IsNotNull isNotNull, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
