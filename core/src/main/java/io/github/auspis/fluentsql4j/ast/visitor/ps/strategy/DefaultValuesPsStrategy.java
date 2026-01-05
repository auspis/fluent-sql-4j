package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dml.component.InsertData.DefaultValues;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface DefaultValuesPsStrategy {
    PreparedStatementSpec handle(DefaultValues defaultValues, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
