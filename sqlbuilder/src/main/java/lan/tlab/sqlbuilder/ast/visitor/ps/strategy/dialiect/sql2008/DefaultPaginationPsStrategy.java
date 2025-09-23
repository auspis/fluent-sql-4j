package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.sqlbuilder.ast.clause.pagination.Pagination;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.PaginationPsStrategy;

public class DefaultPaginationPsStrategy implements PaginationPsStrategy {

    @Override
    public PsDto handle(Pagination pagination, PreparedStatementVisitor visitor, AstContext ctx) {
        StringBuilder sql = new StringBuilder();

        // SQL 2008 standard uses OFFSET ... ROWS FETCH NEXT ... ROWS ONLY
        // Add OFFSET clause if page > 1
        if (pagination.getPage() > 1) {
            long offset = (pagination.getPage() - 1) * pagination.getPerPage();
            sql.append(" OFFSET ").append(offset).append(" ROWS");
        }

        // Add FETCH clause (SQL 2008 standard)
        sql.append(" FETCH NEXT ").append(pagination.getPerPage()).append(" ROWS ONLY");

        return new PsDto(sql.toString(), List.of());
    }
}
