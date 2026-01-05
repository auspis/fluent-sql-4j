package io.github.auspis.fluentsql4j.ast.dql.statement;

import io.github.auspis.fluentsql4j.ast.core.expression.set.SetExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.set.TableExpression;
import io.github.auspis.fluentsql4j.ast.dql.clause.Fetch;
import io.github.auspis.fluentsql4j.ast.dql.clause.From;
import io.github.auspis.fluentsql4j.ast.dql.clause.GroupBy;
import io.github.auspis.fluentsql4j.ast.dql.clause.Having;
import io.github.auspis.fluentsql4j.ast.dql.clause.OrderBy;
import io.github.auspis.fluentsql4j.ast.dql.clause.Select;
import io.github.auspis.fluentsql4j.ast.dql.clause.Where;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
public class SelectStatement implements DataQueryStatement, TableExpression, SetExpression {

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
