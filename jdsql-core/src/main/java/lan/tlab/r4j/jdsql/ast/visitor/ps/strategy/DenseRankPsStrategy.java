package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.DenseRank;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface DenseRankPsStrategy {
    PreparedStatementSpec handle(DenseRank denseRank, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
