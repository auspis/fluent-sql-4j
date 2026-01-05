package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.ddl.statement.CreateTableStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface CreateTableStatementPsStrategy {
    PreparedStatementSpec handle(
            CreateTableStatement createTableStatement,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx);
}
