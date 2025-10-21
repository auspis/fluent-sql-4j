package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.Projection;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.SelectClausePsStrategy;

public class DefaultSelectClausePsStrategy implements SelectClausePsStrategy {
    @Override
    public PsDto handle(Select select, Visitor<PsDto> renderer, AstContext ctx) {
        List<Projection> projections = select.getProjections();
        if (projections.isEmpty()) {
            return new PsDto("*", List.of());
        }

        List<Object> allParameters = new ArrayList<>();
        String sql = projections.stream()
                .map(p -> {
                    PsDto result = p.accept(renderer, ctx);
                    allParameters.addAll(result.parameters());
                    return result.sql();
                })
                .collect(Collectors.joining(", "));
        return new PsDto(sql, allParameters);
    }
}
