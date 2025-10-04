package lan.tlab.r4j.sql.ast.clause.from.source.join;

import lan.tlab.r4j.sql.ast.clause.from.source.FromSource;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OnJoin implements FromSource {

    private final FromSource left;
    private final JoinType type;
    private final FromSource right;
    private final Predicate onCondition;

    public enum JoinType {
        INNER,
        LEFT, // LEFT OUTER
        RIGHT, // RIGHT OUTER
        FULL, // FULL OUTER
        CROSS // CROSS JOIN
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
