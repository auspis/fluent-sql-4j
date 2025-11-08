package lan.tlab.r4j.sql.ast.dql.statement;

import lan.tlab.r4j.sql.ast.common.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.dql.clause.Fetch;
import lan.tlab.r4j.sql.ast.dql.clause.From;
import lan.tlab.r4j.sql.ast.dql.clause.GroupBy;
import lan.tlab.r4j.sql.ast.dql.clause.Having;
import lan.tlab.r4j.sql.ast.dql.clause.OrderBy;
import lan.tlab.r4j.sql.ast.dql.clause.Select;
import lan.tlab.r4j.sql.ast.dql.clause.Where;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
public class SelectStatement implements DataQueryStatement, TableExpression {

    @Default
    private final Select select = new Select();

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
