package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.identifier.Alias;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.AsPsStrategy;

public class DefaultAsPsStrategy implements AsPsStrategy {
    @Override
    public PsDto handle(Alias as, Visitor<PsDto> visitor, AstContext ctx) {
        String sql = "\"" + as.getName() + "\"";
        return new PsDto(sql, List.of());
    }
}
