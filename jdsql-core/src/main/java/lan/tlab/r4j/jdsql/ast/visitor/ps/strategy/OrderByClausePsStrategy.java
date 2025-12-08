package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dql.clause.OrderBy;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface OrderByClausePsStrategy {
    PreparedStatementSpec handle(OrderBy orderBy, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
