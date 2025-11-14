package lan.tlab.r4j.sql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.ddl.definition.DataType.ParameterizedDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;

public interface ParameterizedDataTypePsStrategy {
    PsDto handle(ParameterizedDataType type, PreparedStatementRenderer renderer, AstContext ctx);
}
