package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dml.component.InsertData.InsertValues;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface InsertValuesPsStrategy {
    PreparedStatementSpec handle(InsertValues insertValues, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
