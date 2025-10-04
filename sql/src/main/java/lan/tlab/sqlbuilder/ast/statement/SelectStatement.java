package lan.tlab.sqlbuilder.ast.statement;

import lan.tlab.sqlbuilder.ast.clause.conditional.having.Having;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.fetch.Fetch;
import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy;
import lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.expression.set.TableExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
public class SelectStatement implements Statement, TableExpression {

    @Default
    private final Select select = Select.builder().build();

    @Default
    private final From from = From.builder().build();

    @Default
    private final Where where = Where.builder().build();

    @Default
    private final GroupBy groupBy = GroupBy.builder().build();

    @Default
    private final Having having = Having.builder().build();

    @Default
    private final OrderBy orderBy = OrderBy.builder().build();

    @Default
    private final Fetch fetch = Fetch.builder().build();

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
