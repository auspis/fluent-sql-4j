package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.clause;

import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.Projection;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class SelectRenderStrategy implements ClauseRenderStrategy {

    public String render(Select clause, SqlRenderer sqlRenderer, AstContext ctx) {
        List<Projection> projections = clause.getProjections();
        String values = projections.isEmpty()
                ? "*"
                : clause.getProjections().stream()
                        .map(expr -> expr.accept(sqlRenderer, ctx))
                        .collect(Collectors.joining(", "));
        return String.format("SELECT %s", values);
    }
}
