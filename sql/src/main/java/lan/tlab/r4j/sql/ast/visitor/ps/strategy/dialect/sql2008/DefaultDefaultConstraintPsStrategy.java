package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DefaultConstraintPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;

public class DefaultDefaultConstraintPsStrategy implements DefaultConstraintPsStrategy {

    private final SqlRenderer sqlRenderer = SqlRendererFactory.standardSql2008();

    @Override
    public PsDto handle(DefaultConstraintDefinition constraint, PreparedStatementRenderer renderer, AstContext ctx) {
        // Default constraints are static DDL elements without parameters
        String sql = constraint.accept(sqlRenderer, ctx);
        return new PsDto(sql, List.of());
    }
}
