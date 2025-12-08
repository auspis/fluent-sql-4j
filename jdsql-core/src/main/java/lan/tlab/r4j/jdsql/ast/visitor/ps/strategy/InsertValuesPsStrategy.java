package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.InsertValues;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface InsertValuesPsStrategy {
    PreparedStatementSpec handle(InsertValues insertValues, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
