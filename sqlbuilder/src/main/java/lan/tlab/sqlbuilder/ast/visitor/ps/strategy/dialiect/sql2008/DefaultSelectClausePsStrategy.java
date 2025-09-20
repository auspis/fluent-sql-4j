package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.Projection;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.SelectClausePsStrategy;

public class DefaultSelectClausePsStrategy implements SelectClausePsStrategy {
    @Override
    public PsDto handle(Select select, Visitor<PsDto> visitor, AstContext ctx) {
        List<String> cols = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if (select.getProjections().isEmpty()) {
            // No projections: SELECT *
            return new PsDto("*", List.of());
        }
        for (Projection p : select.getProjections()) {
            PsDto res = p.accept(visitor, ctx);
            cols.add(res.sql());
            params.addAll(res.parameters());
        }
        String sql = String.join(", ", cols);
        return new PsDto(sql, params);
    }
}
