package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class FromRenderStrategy implements ClauseRenderStrategy {

    public String render(From clause, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "FROM %s",
                clause.getSources().stream()
                        .map(src -> src.accept(sqlRenderer, ctx))
                        .collect(Collectors.joining(", ")));
    }
}
