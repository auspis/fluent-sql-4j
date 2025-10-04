package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.item.As;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.AsPsStrategy;

public class DefaultAsPsStrategy implements AsPsStrategy {
    @Override
    public PsDto handle(As as, Visitor<PsDto> visitor, AstContext ctx) {
        String sql = "\"" + as.getName() + "\"";
        return new PsDto(sql, List.of());
    }
}
