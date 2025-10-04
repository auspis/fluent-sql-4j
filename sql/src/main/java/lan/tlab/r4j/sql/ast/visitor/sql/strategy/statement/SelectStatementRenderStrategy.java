package lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class SelectStatementRenderStrategy implements StatementRenderStrategy {

    public String render(SelectStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        return Stream.of(
                        statement.getSelect(),
                        statement.getFrom(),
                        statement.getWhere(),
                        statement.getGroupBy(),
                        statement.getHaving(),
                        statement.getOrderBy(),
                        statement.getFetch())
                .map(clause -> clause.accept(sqlRenderer, ctx))
                .filter(s -> !Objects.isNull(s))
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "));
    }
}
