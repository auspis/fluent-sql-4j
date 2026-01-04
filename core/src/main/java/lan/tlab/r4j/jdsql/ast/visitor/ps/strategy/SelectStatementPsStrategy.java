package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface SelectStatementPsStrategy {
    PreparedStatementSpec handle(
            SelectStatement selectStatement, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
