package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.Collections;
import lan.tlab.r4j.sql.ast.statement.CreateTableStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CreateTableStatementPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;

public class DefaultCreateTableStatementPsStrategy implements CreateTableStatementPsStrategy {

    @Override
    public PsDto handle(CreateTableStatement createTableStatement, PreparedStatementVisitor visitor, AstContext ctx) {
        // For CREATE TABLE statements, we use the SQL renderer since DDL statements typically don't have parameters
        String sql = String.format(
                "CREATE TABLE %s",
                createTableStatement.getTableDefinition().accept(SqlRendererFactory.standardSql2008(), ctx));

        return new PsDto(sql, Collections.emptyList());
    }
}
