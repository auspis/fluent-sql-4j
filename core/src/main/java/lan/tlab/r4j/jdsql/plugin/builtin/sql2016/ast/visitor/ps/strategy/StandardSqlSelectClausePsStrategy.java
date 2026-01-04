package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.dql.projection.Projection;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.SelectClausePsStrategy;

public class StandardSqlSelectClausePsStrategy implements SelectClausePsStrategy {
    @Override
    public PreparedStatementSpec handle(Select select, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        List<Projection> projections = select.projections();
        if (projections.isEmpty()) {
            return new PreparedStatementSpec("*", List.of());
        }

        List<Object> allParameters = new ArrayList<>();
        String sql = projections.stream()
                .map(p -> {
                    PreparedStatementSpec result = p.accept(renderer, ctx);
                    allParameters.addAll(result.parameters());
                    return result.sql();
                })
                .collect(Collectors.joining(", "));
        return new PreparedStatementSpec(sql, allParameters);
    }
}
