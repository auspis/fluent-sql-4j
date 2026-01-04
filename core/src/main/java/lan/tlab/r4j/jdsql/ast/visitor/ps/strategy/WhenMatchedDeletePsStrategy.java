package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenMatchedDelete;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface WhenMatchedDeletePsStrategy {

    PreparedStatementSpec handle(WhenMatchedDelete item, AstToPreparedStatementSpecVisitor visitor, AstContext ctx);
}
