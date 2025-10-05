package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertSource;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.AstContext.Scope;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.InsertSourcePsStrategy;

public class DefaultInsertSourcePsStrategy implements InsertSourcePsStrategy {
    @Override
    public PsDto handle(InsertSource item, Visitor<PsDto> visitor, AstContext ctx) {
        PsDto psDto = item.getSetExpression().accept(visitor, new AstContext(Scope.UNION));
        return new PsDto(psDto.sql(), psDto.parameters());
    }
}
