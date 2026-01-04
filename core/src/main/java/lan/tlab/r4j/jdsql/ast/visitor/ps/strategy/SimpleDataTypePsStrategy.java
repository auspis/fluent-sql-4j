package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface SimpleDataTypePsStrategy {
    PreparedStatementSpec handle(
            SimpleDataType simpleDataType, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx);
}
