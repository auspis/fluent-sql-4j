package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.ddl.definition.DataType.SimpleDataType;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.SimpleDataTypePsStrategy;
import java.util.List;

public class StandardSqlSimpleDataTypePsStrategy implements SimpleDataTypePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            SimpleDataType type, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        // Simple data types are static DDL elements without parameters
        // Inline rendering logic from StandardSqlSimpleDataTypeRenderStrategy
        return new PreparedStatementSpec(type.name(), List.of());
    }
}
