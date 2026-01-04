package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.datetime.ExtractDatePart;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface ExtractDatePartPsStrategy {
    PreparedStatementSpec handle(
            ExtractDatePart extractDatePart, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
