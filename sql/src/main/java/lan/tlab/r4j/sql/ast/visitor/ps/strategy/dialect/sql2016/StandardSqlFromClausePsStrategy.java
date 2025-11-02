package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.FromClausePsStrategy;

public class StandardSqlFromClausePsStrategy implements FromClausePsStrategy {
    @Override
    public PsDto handle(From clause, Visitor<PsDto> renderer, AstContext ctx) {
        List<String> sqlParts = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (var source : clause.sources()) {
            PsDto res = source.accept(renderer, ctx);
            sqlParts.add(res.sql());
            params.addAll(res.parameters());
        }
        String sql = String.join(", ", sqlParts);
        return new PsDto(sql, params);
    }
}
