package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import java.util.List;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.SimpleDataTypePsStrategy;

public class StandardSqlSimpleDataTypePsStrategy implements SimpleDataTypePsStrategy {

    @Override
    public PsDto handle(SimpleDataType simpleDataType, PreparedStatementRenderer renderer, AstContext ctx) {
        // Simple data types are static DDL elements without parameters
        // Just return the type name
        return new PsDto(simpleDataType.name(), List.of());
    }
}
