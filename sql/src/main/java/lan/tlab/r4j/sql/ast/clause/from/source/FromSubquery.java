package lan.tlab.r4j.sql.ast.clause.from.source;

import lan.tlab.r4j.sql.ast.expression.item.As;
import lan.tlab.r4j.sql.ast.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FromSubquery implements FromSource {

    private final TableExpression subquery;

    @Default
    private final As as = As.nullObject();

    public static FromSubquery of(TableExpression subquery, String as) {
        return of(subquery, new As(as));
    }

    public static FromSubquery of(TableExpression subquery, As as) {
        return builder().subquery(subquery).as(as).build();
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
