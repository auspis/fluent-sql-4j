package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.JsonExists;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface JsonExistsPsStrategy {
    PreparedStatementSpec handle(
            JsonExists jsonExists, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
