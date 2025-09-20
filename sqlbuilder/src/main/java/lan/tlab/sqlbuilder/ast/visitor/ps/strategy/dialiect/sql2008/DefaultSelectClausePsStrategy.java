package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.Projection;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.SelectClausePsStrategy;

public class DefaultSelectClausePsStrategy implements SelectClausePsStrategy {
    @Override
    public PsDto handle(Select select, Visitor<PsDto> visitor, AstContext ctx) {
        List<Projection> projections = select.getProjections();
        if (projections.isEmpty()) {
            return new PsDto("*", List.of());
        }

        String sql = projections.stream().map(p -> p.accept(visitor, ctx).sql()).collect(Collectors.joining(", "));
        return new PsDto(sql, List.of());
    }
}
