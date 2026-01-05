package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.function.number.Mod;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface ModPsStrategy {

    PreparedStatementSpec handle(Mod mod, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
