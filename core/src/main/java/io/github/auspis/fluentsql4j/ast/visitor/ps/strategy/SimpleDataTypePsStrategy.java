package io.github.auspis.fluentsql4j.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.ddl.definition.DataType.SimpleDataType;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface SimpleDataTypePsStrategy {
    PreparedStatementSpec handle(
            SimpleDataType simpleDataType, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
