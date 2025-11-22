package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.DataType.ParameterizedDataType;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

public interface ParameterizedDataTypePsStrategy {
    PsDto handle(ParameterizedDataType type, PreparedStatementRenderer renderer, AstContext ctx);
}
