package lan.tlab.r4j.sql.ast.statement.dql;

import lan.tlab.r4j.sql.ast.clause.conditional.having.Having;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.clause.fetch.Fetch;
import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.groupby.GroupBy;
import lan.tlab.r4j.sql.ast.clause.orderby.OrderBy;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
public class SelectStatement implements DataQueryStatement, TableExpression {

    @Default
    private final Select select = Select.builder().build();

    @Default
    private final From from = From.nullObject();

    @Default
    private final Where where = Where.nullObject();

    @Default
    private final GroupBy groupBy = GroupBy.nullObject();

    @Default
    private final Having having = Having.nullObject();

    @Default
    private final OrderBy orderBy = OrderBy.nullObject();

    @Default
    private final Fetch fetch = Fetch.nullObject();

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
