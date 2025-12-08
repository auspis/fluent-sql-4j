package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface WhenMatchedUpdatePsStrategy {

    PreparedStatementSpec handle(WhenMatchedUpdate item, PreparedStatementRenderer visitor, AstContext ctx);
}
