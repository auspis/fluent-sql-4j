package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.IndexDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface IndexDefinitionPsStrategy {
    PreparedStatementSpec handle(
            IndexDefinition indexDefinition, AstToPreparedStatementSpecVisitor renderer, AstContext ctx);
}
