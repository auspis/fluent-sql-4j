package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause;

import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.Projection;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class SelectRenderStrategy implements ClauseRenderStrategy {

    public String render(Select clause, SqlRenderer sqlRenderer) {
        List<Projection> projections = clause.getProjections();
        String values = projections.isEmpty()
                ? "*"
                : clause.getProjections().stream()
                        .map(expr -> expr.accept(sqlRenderer))
                        .collect(Collectors.joining(", "));
        return String.format("SELECT %s", values);
    }
}
