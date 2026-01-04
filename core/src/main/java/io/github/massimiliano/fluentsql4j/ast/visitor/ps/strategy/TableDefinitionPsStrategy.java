package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.ddl.definition.TableDefinition;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface TableDefinitionPsStrategy {
    PreparedStatementSpec handle(
            TableDefinition tableDefinition, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
