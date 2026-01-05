package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarSubquery;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface ScalarSubqueryPsStrategy {
    PreparedStatementSpec handle(
            ScalarSubquery subquery, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
