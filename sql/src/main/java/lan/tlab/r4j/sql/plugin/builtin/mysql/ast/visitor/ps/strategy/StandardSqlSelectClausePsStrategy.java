package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.dql.clause.Select;
import lan.tlab.r4j.sql.ast.dql.projection.Projection;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.SelectClausePsStrategy;

public class StandardSqlSelectClausePsStrategy implements SelectClausePsStrategy {
    @Override
    public PsDto handle(Select select, Visitor<PsDto> renderer, AstContext ctx) {
        List<Projection> projections = select.projections();
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
