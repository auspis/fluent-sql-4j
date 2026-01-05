package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.function.string.UnaryString;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface UnaryStringPsStrategy {

    PreparedStatementSpec handle(
            UnaryString functionCall, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
