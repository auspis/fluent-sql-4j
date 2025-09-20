package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.LiteralPsStrategy;

public class DefaultLiteralPsStrategy implements LiteralPsStrategy {
    @Override
    public PsDto handle(Literal<?> literal, Visitor<PsDto> visitor, AstContext ctx) {
        return new PsDto("?", List.of(literal.getValue()));
    }
}
