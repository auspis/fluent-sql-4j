package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.expression.set.ExceptExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface ExceptExpressionPsStrategy {

    PreparedStatementSpec handle(
            ExceptExpression expression, AstToPreparedStatementSpecVisitor renderer, AstContext ctx);
}
