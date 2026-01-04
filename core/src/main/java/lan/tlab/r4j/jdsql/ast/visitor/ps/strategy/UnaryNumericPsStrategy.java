package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.function.number.UnaryNumeric;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface UnaryNumericPsStrategy {

    PreparedStatementSpec handle(
            UnaryNumeric functionCall, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
