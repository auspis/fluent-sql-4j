package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LiteralPsStrategy;

public class StandardSqlLiteralPsStrategy implements LiteralPsStrategy {
    @Override
    public PsDto handle(Literal<?> literal, Visitor<PsDto> renderer, AstContext ctx) {
        return new PsDto("?", List.of(literal.value()));
    }
}
