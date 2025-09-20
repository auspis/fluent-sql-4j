package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.FromClausePsStrategy;

public class DefaultFromClausePsStrategy implements FromClausePsStrategy {
    @Override
    public PsDto handle(From clause, Visitor<PsDto> visitor, AstContext ctx) {
        List<String> sqlParts = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (var source : clause.getSources()) {
            PsDto res = source.accept(visitor, ctx);
            sqlParts.add(res.sql());
            params.addAll(res.parameters());
        }
        String sql = String.join(", ", sqlParts);
        return new PsDto(sql, params);
    }
}
