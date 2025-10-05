package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UniqueConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;

public class DefaultUniqueConstraintPsStrategy implements UniqueConstraintPsStrategy {

    private final SqlRenderer sqlRenderer = SqlRendererFactory.standardSql2008();

    @Override
    public PsDto handle(UniqueConstraintDefinition constraint, PreparedStatementVisitor visitor, AstContext ctx) {
        // Unique constraints are static DDL elements without parameters
        String sql = constraint.accept(sqlRenderer, ctx);
        return new PsDto(sql, List.of());
    }
}
