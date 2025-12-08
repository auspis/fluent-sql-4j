package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface NullPredicatePsStrategy {
    PreparedStatementSpec handle(NullPredicate expression, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
