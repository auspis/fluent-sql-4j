package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.IndexDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.IndexDefinitionPsStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;

public class DefaultIndexDefinitionPsStrategy implements IndexDefinitionPsStrategy {

    private final SqlRenderer sqlRenderer = SqlRendererFactory.standardSql2008();

    @Override
    public PsDto handle(IndexDefinition index, PreparedStatementRenderer renderer, AstContext ctx) {
        // Index definitions are static DDL elements without parameters
        String sql = index.accept(sqlRenderer, ctx);
        return new PsDto(sql, List.of());
    }
}
