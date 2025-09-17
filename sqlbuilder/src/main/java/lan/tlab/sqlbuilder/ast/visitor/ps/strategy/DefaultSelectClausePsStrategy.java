package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.Projection;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.PreparedSqlResult;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;

public class DefaultSelectClausePsStrategy implements SelectClausePsStrategy {
    @Override
    public PreparedSqlResult handle(Select select, SqlVisitor<PreparedSqlResult> visitor, AstContext ctx) {
        List<String> cols = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if (select.getProjections().isEmpty()) {
            // No projections: SELECT *
            return new PreparedSqlResult("*", List.of());
        }
        for (Projection p : select.getProjections()) {
            PreparedSqlResult res = p.accept(visitor, ctx);
            cols.add(res.sql());
            params.addAll(res.parameters());
        }
        String sql = String.join(", ", cols);
        return new PreparedSqlResult(sql, params);
    }
}
