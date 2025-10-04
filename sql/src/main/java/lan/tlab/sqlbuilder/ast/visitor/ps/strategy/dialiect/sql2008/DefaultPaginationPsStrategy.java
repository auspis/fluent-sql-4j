package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.sqlbuilder.ast.clause.fetch.Fetch;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.FetchPsStrategy;

public class DefaultPaginationPsStrategy implements FetchPsStrategy {

    @Override
    public PsDto handle(Fetch pagination, PreparedStatementVisitor visitor, AstContext ctx) {
        StringBuilder sql = new StringBuilder();

        // SQL 2008 standard uses OFFSET ... ROWS FETCH NEXT ... ROWS ONLY
        // Add OFFSET clause if offset > 0
        if (pagination.getOffset() > 0) {
            sql.append(" OFFSET ").append(pagination.getOffset()).append(" ROWS");
        }

        // Add FETCH clause (SQL 2008 standard)
        sql.append(" FETCH NEXT ").append(pagination.getRows()).append(" ROWS ONLY");

        return new PsDto(sql.toString(), List.of());
    }
}
