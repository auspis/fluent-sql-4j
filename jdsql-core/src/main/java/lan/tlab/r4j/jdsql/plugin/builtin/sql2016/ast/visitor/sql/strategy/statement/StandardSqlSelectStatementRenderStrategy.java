package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.statement.SelectStatementRenderStrategy;

public class StandardSqlSelectStatementRenderStrategy implements SelectStatementRenderStrategy {

    @Override
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
