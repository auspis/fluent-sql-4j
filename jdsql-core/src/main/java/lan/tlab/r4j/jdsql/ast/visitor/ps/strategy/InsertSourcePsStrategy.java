package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.InsertSource;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface InsertSourcePsStrategy {
    PreparedStatementSpec handle(InsertSource insertSource, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
