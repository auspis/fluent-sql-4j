package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause;

import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.Projection;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.SelectRenderStrategy;

public class StandardSqlSelectRenderStrategy implements SelectRenderStrategy {

    @Override
    public String render(Select clause, SqlRenderer sqlRenderer, AstContext ctx) {
        List<Projection> projections = clause.projections();
        String values = projections.isEmpty()
                ? "*"
                : clause.projections().stream()
                        .map(expr -> expr.accept(sqlRenderer, ctx))
                        .collect(Collectors.joining(", "));
        return String.format("SELECT %s", values);
    }
}
