package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.ddl.definition.ReferencesItem;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface ReferencesItemPsStrategy {

    PreparedStatementSpec handle(
            ReferencesItem item, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
