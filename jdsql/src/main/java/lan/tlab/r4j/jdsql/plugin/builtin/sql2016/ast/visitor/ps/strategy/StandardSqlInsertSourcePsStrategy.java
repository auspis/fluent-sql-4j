package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.InsertSource;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext.Scope;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.InsertSourcePsStrategy;

public class StandardSqlInsertSourcePsStrategy implements InsertSourcePsStrategy {
    @Override
    public PsDto handle(InsertSource item, Visitor<PsDto> renderer, AstContext ctx) {
        PsDto psDto = item.setExpression().accept(renderer, new AstContext(Scope.UNION));
        return new PsDto(psDto.sql(), psDto.parameters());
    }
}
