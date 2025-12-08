package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface TablePsStrategy {
    PreparedStatementSpec handle(TableIdentifier table, Visitor<PreparedStatementSpec> visitor, AstContext ctx);
}
