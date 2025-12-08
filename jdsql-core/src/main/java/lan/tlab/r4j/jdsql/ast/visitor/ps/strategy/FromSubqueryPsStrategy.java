package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dql.source.FromSubquery;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface FromSubqueryPsStrategy {
    PreparedStatementSpec handle(FromSubquery fromSubquery, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
