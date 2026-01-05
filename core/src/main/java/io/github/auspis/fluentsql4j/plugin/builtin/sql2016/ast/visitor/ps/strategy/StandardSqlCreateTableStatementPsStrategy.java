package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.ddl.statement.CreateTableStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CreateTableStatementPsStrategy;

public class StandardSqlCreateTableStatementPsStrategy implements CreateTableStatementPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            CreateTableStatement createTableStatement,
            AstToPreparedStatementSpecVisitor astToPsSpecVisitor,
            AstContext ctx) {
        PreparedStatementSpec tableDefinitionDto =
                createTableStatement.tableDefinition().accept(astToPsSpecVisitor, ctx);
        String sql = "CREATE TABLE " + tableDefinitionDto.sql();
        return new PreparedStatementSpec(sql, tableDefinitionDto.parameters());
    }
}
