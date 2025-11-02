package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import java.util.List;
import lan.tlab.r4j.sql.ast.clause.fetch.Fetch;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.FetchPsStrategy;

public class StandardSqlPaginationPsStrategy implements FetchPsStrategy {

    @Override
    public PsDto handle(Fetch pagination, PreparedStatementRenderer renderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder();

        // SQL 2008 standard uses OFFSET ... ROWS FETCH NEXT ... ROWS ONLY
        // Add OFFSET clause if offset > 0
        if (pagination.offset() > 0) {
            sql.append(" OFFSET ").append(pagination.offset()).append(" ROWS");
        }

        // Add FETCH clause (SQL 2008 standard)
        sql.append(" FETCH NEXT ").append(pagination.rows()).append(" ROWS ONLY");

        return new PsDto(sql.toString(), List.of());
    }
}
