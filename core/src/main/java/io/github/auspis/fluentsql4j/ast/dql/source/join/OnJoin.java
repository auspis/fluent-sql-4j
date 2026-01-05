package io.github.auspis.fluentsql4j.ast.dql.source.join;

import io.github.auspis.fluentsql4j.ast.core.predicate.Predicate;
import io.github.auspis.fluentsql4j.ast.dql.source.FromSource;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

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
