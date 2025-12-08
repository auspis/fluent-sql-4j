package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.window.RowNumber;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface RowNumberPsStrategy {
    PreparedStatementSpec handle(RowNumber rowNumber, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
