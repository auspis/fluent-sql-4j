package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.function.number.UnaryNumeric;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface UnaryNumericPsStrategy {

    PreparedStatementSpec handle(
            UnaryNumeric functionCall, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
