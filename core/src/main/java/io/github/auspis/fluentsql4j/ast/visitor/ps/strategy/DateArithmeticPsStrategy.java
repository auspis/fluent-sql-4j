package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.DateArithmetic;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface DateArithmeticPsStrategy {
    PreparedStatementSpec handle(
            DateArithmetic dateArithmetic, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
