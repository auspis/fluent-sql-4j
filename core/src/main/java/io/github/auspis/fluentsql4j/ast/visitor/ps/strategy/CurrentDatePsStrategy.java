package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.CurrentDate;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface CurrentDatePsStrategy {
    PreparedStatementSpec handle(
            CurrentDate currentDate, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
