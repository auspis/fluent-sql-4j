package lan.tlab.r4j.sql.ast.clause.conditional.where;

import lan.tlab.r4j.sql.ast.clause.Clause;
import lan.tlab.r4j.sql.ast.predicate.NullPredicate;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.predicate.logical.AndOr;
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
public class Where implements Clause {

    @Default
    private final Predicate condition = new NullPredicate();

    public static Where andOf(Predicate... conditions) {
        return builder().condition(AndOr.and(conditions)).build();
    }

    public static Where of(Predicate condition) {
        return builder().condition(condition).build();
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
