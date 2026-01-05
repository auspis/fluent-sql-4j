package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.ddl.definition.ColumnDefinition;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface ColumnDefinitionPsStrategy {
    PreparedStatementSpec handle(
            ColumnDefinition columnDefinition, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
