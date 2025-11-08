package lan.tlab.r4j.sql.ast.dql.source.join;

import lan.tlab.r4j.sql.ast.common.predicate.Predicate;
import lan.tlab.r4j.sql.ast.dql.source.FromSource;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record OnJoin(FromSource left, JoinType type, FromSource right, Predicate onCondition) implements FromSource {

    public enum JoinType {
        INNER,
        LEFT,
        RIGHT,
        FULL,
        CROSS
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
