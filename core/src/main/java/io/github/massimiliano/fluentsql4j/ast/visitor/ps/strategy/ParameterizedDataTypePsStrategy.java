package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.ddl.definition.DataType.ParameterizedDataType;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface ParameterizedDataTypePsStrategy {
    PreparedStatementSpec handle(
            ParameterizedDataType type, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
