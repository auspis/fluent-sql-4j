package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertSource;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.InsertSourcePsStrategy;

public class DefaultInsertSourcePsStrategy implements InsertSourcePsStrategy {
    @Override
    public PsDto handle(InsertSource insertSource, Visitor<PsDto> visitor, AstContext ctx) {
        // InsertSource is a parent type, delegate to the actual subtype
        return insertSource.accept(visitor, ctx);
    }
}
