package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dml.statement.DeleteStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface DeleteStatementPsStrategy {
    PreparedStatementSpec handle(
            DeleteStatement deleteStatement, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
