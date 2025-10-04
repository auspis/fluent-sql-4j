package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.item.ddl.Constraint.PrimaryKey;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.PrimaryKeyPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;

public class DefaultPrimaryKeyPsStrategy implements PrimaryKeyPsStrategy {

    private final SqlRenderer sqlRenderer = SqlRendererFactory.standardSql2008();

    @Override
    public PsDto handle(PrimaryKey item, PreparedStatementVisitor visitor, AstContext ctx) {
        // PrimaryKey constraints are static DDL elements without parameters
        String sql = item.accept(sqlRenderer, ctx);
        return new PsDto(sql, List.of());
    }
}
