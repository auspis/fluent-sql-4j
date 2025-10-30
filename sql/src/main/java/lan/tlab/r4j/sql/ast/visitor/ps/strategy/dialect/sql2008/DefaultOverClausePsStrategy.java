package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.OverClause;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.OverClausePsStrategy;

public class DefaultOverClausePsStrategy implements OverClausePsStrategy {

    @Override
    public PsDto handle(OverClause overClause, Visitor<PsDto> visitor, AstContext ctx) {
        StringBuilder sql = new StringBuilder("OVER (");
        List<Object> parameters = new ArrayList<>();

        if (overClause.partitionBy() != null && !overClause.partitionBy().isEmpty()) {
            List<PsDto> partitionResults = overClause.partitionBy().stream()
                    .map(expr -> expr.accept(visitor, ctx))
                    .toList();

            sql.append("PARTITION BY ");
            sql.append(partitionResults.stream().map(PsDto::sql).collect(Collectors.joining(", ")));

            partitionResults.forEach(result -> parameters.addAll(result.parameters()));
        }

        if (overClause.orderBy() != null && !overClause.orderBy().isEmpty()) {
            if (overClause.partitionBy() != null && !overClause.partitionBy().isEmpty()) {
                sql.append(" ");
            }

            List<PsDto> orderResults = overClause.orderBy().stream()
                    .map(sort -> sort.accept(visitor, ctx))
                    .toList();

            sql.append("ORDER BY ");
            sql.append(orderResults.stream().map(PsDto::sql).collect(Collectors.joining(", ")));

            orderResults.forEach(result -> parameters.addAll(result.parameters()));
        }

        sql.append(")");
        return new PsDto(sql.toString(), parameters);
    }
}
