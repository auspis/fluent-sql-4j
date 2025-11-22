package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause;

import java.util.stream.Collectors;
import lan.tlab.r4j.jdsql.ast.dql.clause.From;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.FromRenderStrategy;

public class StandardSqlFromRenderStrategy implements FromRenderStrategy {

    @Override
    public String render(From clause, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "FROM %s",
                clause.sources().stream()
                        .map(src -> src.accept(sqlRenderer, ctx))
                        .collect(Collectors.joining(", ")));
    }
}
