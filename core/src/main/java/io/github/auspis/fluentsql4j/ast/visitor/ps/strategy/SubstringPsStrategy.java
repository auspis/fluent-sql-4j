package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.function.string.Substring;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface SubstringPsStrategy {
    PreparedStatementSpec handle(
            Substring substring, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
