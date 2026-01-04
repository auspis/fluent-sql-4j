package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.string.CharacterLength;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface CharacterLengthPsStrategy {
    PreparedStatementSpec handle(
            CharacterLength characterLength, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
