package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.DataType.ParameterizedDataType;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface ParameterizedDataTypePsStrategy {
    PreparedStatementSpec handle(ParameterizedDataType type, PreparedStatementRenderer renderer, AstContext ctx);
}
