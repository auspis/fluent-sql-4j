package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.LiteralPsStrategy;

public class StandardSqlLiteralPsStrategy implements LiteralPsStrategy {
    @Override
    public PsDto handle(Literal<?> literal, Visitor<PsDto> renderer, AstContext ctx) {
        return new PsDto("?", List.of(literal.value()));
    }
}
