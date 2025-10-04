package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.item.ddl.DataType.SimpleDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.SimpleDataTypePsStrategy;

public class DefaultSimpleDataTypePsStrategy implements SimpleDataTypePsStrategy {

    @Override
    public PsDto handle(SimpleDataType simpleDataType, PreparedStatementVisitor visitor, AstContext ctx) {
        // Simple data types are static DDL elements without parameters
        // Just return the type name
        return new PsDto(simpleDataType.getName(), List.of());
    }
}
