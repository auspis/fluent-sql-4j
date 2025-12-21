package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ReferencesItem;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface ReferencesItemPsStrategy {

    PreparedStatementSpec handle(
            ReferencesItem item, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
