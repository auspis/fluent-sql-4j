package lan.tlab.r4j.sql.dsl.select;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.predicate.Comparison;
import lan.tlab.r4j.sql.ast.common.predicate.Predicate;
import lan.tlab.r4j.sql.ast.dql.source.FromSource;
import lan.tlab.r4j.sql.ast.dql.source.join.OnJoin;

public class JoinBuilder {
    private final SelectBuilder parent;
    private final FromSource left;
    private final OnJoin.JoinType joinType;
    private final FromSource right;

    public JoinBuilder(SelectBuilder parent, FromSource left, OnJoin.JoinType joinType, FromSource right) {
        this.parent = parent;
        this.left = left;
        this.joinType = joinType;
        this.right = right;
    }

    public SelectBuilder on(ColumnReference leftColumn, ColumnReference rightColumn) {
        if (leftColumn == null) {
            throw new IllegalArgumentException("Left column cannot be null");
        }
        if (rightColumn == null) {
            throw new IllegalArgumentException("Right column cannot be null");
        }

        Predicate onCondition = Comparison.eq(leftColumn, rightColumn);
        OnJoin join = new OnJoin(left, joinType, right, onCondition);

        return parent.addJoin(join);
    }
}
