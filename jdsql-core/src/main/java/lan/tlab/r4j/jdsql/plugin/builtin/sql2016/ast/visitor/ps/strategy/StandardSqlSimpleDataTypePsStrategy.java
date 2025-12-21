package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.SimpleDataTypePsStrategy;

public class StandardSqlSimpleDataTypePsStrategy implements SimpleDataTypePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            SimpleDataType type, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        // Simple data types are static DDL elements without parameters
        // Inline rendering logic from StandardSqlSimpleDataTypeRenderStrategy
        return new PreparedStatementSpec(type.name(), List.of());
    }
}
