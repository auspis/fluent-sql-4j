package lan.tlab.sqlbuilder.ast.clause.from.source;

import lan.tlab.sqlbuilder.ast.expression.item.As;
import lan.tlab.sqlbuilder.ast.expression.set.TableExpression;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
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
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
