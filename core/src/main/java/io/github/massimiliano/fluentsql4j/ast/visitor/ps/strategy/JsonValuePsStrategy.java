package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.JsonValue;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface JsonValuePsStrategy {
    PreparedStatementSpec handle(
            JsonValue jsonValue, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
