package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertSource;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.AstContext.Scope;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.InsertSourcePsStrategy;

public class DefaultInsertSourcePsStrategy implements InsertSourcePsStrategy {
    @Override
    public PsDto handle(InsertSource item, Visitor<PsDto> visitor, AstContext ctx) {
        PsDto psDto = item.getSetExpression().accept(visitor, new AstContext(Scope.UNION));
        return new PsDto(psDto.sql(), psDto.parameters());
    }
}
