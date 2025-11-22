package lan.tlab.r4j.jdsql.dsl.select;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.dql.source.FromSource;
import lan.tlab.r4j.jdsql.ast.dql.source.join.OnJoin;
import lan.tlab.r4j.jdsql.dsl.util.ColumnReferenceUtil;

public class JoinSpecBuilder {
    private final SelectBuilder parent;
    private final FromSource left;
    private final OnJoin.JoinType joinType;
    private final String rightTableName;
    private String rightTableAlias;

    public JoinSpecBuilder(SelectBuilder parent, FromSource left, OnJoin.JoinType joinType, String rightTableName) {
        this.parent = parent;
        this.left = left;
        this.joinType = joinType;
        this.rightTableName = rightTableName;
    }

    public JoinSpecBuilder as(String alias) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        this.rightTableAlias = alias;
        return this;
    }

    public SelectBuilder on(String leftColumn, String rightColumn) {
        if (leftColumn == null || leftColumn.trim().isEmpty()) {
            throw new IllegalArgumentException("Left column cannot be null or empty");
        }
        if (rightColumn == null || rightColumn.trim().isEmpty()) {
            throw new IllegalArgumentException("Right column cannot be null or empty");
        }

        ColumnReference leftColRef = ColumnReferenceUtil.parseColumnReference(leftColumn, "");
        ColumnReference rightColRef = ColumnReferenceUtil.parseColumnReference(rightColumn, "");

        FromSource right = rightTableAlias != null
                ? new TableIdentifier(rightTableName, rightTableAlias)
                : new TableIdentifier(rightTableName);

        JoinBuilder joinBuilder = new JoinBuilder(parent, left, joinType, right);
        return joinBuilder.on(leftColRef, rightColRef);
    }
}
