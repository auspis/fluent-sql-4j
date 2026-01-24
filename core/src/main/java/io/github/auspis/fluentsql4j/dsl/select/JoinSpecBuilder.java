package io.github.auspis.fluentsql4j.dsl.select;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.dql.source.FromSource;
import io.github.auspis.fluentsql4j.ast.dql.source.join.OnJoin;
import io.github.auspis.fluentsql4j.dsl.util.ColumnReferenceUtil;

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

    public SelectBuilder on(
            String leftTableReference, String leftColumn, String rightTableReference, String rightColumn) {
        ColumnReference leftColRef = ColumnReferenceUtil.createValidated(leftTableReference, leftColumn);
        ColumnReference rightColRef = ColumnReferenceUtil.createValidated(rightTableReference, rightColumn);

        FromSource right = rightTableAlias != null
                ? new TableIdentifier(rightTableName, rightTableAlias)
                : new TableIdentifier(rightTableName);

        JoinBuilder joinBuilder = new JoinBuilder(parent, left, joinType, right);
        return joinBuilder.on(leftColRef, rightColRef);
    }
}
