package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dml.component.InsertData.InsertSource;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface InsertSourcePsStrategy {
    PreparedStatementSpec handle(InsertSource insertSource, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
