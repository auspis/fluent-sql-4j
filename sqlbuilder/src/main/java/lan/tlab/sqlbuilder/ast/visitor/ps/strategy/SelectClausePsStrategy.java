package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.PreparedSqlResult;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;

public interface SelectClausePsStrategy {
    PreparedSqlResult handle(Select select, SqlVisitor<PreparedSqlResult> visitor, AstContext ctx);
}
