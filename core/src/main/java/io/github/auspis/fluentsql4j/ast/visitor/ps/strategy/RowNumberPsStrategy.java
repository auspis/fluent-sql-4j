package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.window.RowNumber;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface RowNumberPsStrategy {
    PreparedStatementSpec handle(RowNumber rowNumber, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
