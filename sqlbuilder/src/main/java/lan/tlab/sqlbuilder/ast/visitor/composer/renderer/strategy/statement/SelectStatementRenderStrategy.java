package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.statement;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class SelectStatementRenderStrategy implements StatementRenderStrategy {

    public String render(SelectStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        return Stream.of(
                        statement.getSelect(),
                        statement.getFrom(),
                        statement.getWhere(),
                        statement.getGroupBy(),
                        statement.getHaving(),
                        statement.getOrderBy(),
                        statement.getPagination())
                .map(clause -> clause.accept(sqlRenderer, ctx))
                .filter(s -> !Objects.isNull(s))
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "));
    }
}
