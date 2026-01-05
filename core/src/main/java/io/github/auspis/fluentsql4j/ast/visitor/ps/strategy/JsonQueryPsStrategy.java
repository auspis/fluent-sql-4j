package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.function.json.JsonQuery;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface JsonQueryPsStrategy {
    PreparedStatementSpec handle(
            JsonQuery jsonQuery, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
