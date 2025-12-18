package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface ColumnDefinitionPsStrategy {
    PreparedStatementSpec handle(
            ColumnDefinition columnDefinition, AstToPreparedStatementSpecVisitor renderer, AstContext ctx);
}
