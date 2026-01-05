package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dml.component.MergeUsing;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface MergeUsingPsStrategy {

    PreparedStatementSpec handle(MergeUsing item, AstToPreparedStatementSpecVisitor visitor, AstContext ctx);
}
