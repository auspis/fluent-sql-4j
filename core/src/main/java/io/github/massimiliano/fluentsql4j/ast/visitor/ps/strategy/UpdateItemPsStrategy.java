package io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public interface UpdateItemPsStrategy {

    PreparedStatementSpec handle(UpdateItem item, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
