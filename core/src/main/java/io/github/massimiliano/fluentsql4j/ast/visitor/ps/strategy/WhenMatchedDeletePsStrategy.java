package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedDelete;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface WhenMatchedDeletePsStrategy {

    PreparedStatementSpec handle(WhenMatchedDelete item, AstToPreparedStatementSpecVisitor visitor, AstContext ctx);
}
