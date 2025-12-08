package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dml.statement.InsertStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface InsertStatementPsStrategy {
    PreparedStatementSpec handle(
            InsertStatement insertStatement, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
