package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.ddl.definition.IndexDefinition;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface IndexDefinitionPsStrategy {
    PreparedStatementSpec handle(
            IndexDefinition indexDefinition, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
