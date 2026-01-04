package io.github.massimiliano.fluentsql4j.dsl.select;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.massimiliano.fluentsql4j.ast.dql.source.FromSource;
import io.github.massimiliano.fluentsql4j.ast.dql.source.join.OnJoin;

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
        if (leftTableReference == null || leftTableReference.trim().isEmpty()) {
            throw new IllegalArgumentException("Left table reference cannot be null or empty");
        }
        if (leftTableReference.contains(".")) {
            throw new IllegalArgumentException(
                    "Left table reference must not contain dot: '" + leftTableReference + "'");
        }
        if (leftColumn == null || leftColumn.trim().isEmpty()) {
            throw new IllegalArgumentException("Left column cannot be null or empty");
        }
        if (leftColumn.contains(".")) {
            throw new IllegalArgumentException(
                    "Left column must not contain dot. Use on(table, column, table, column) with separate parameters");
        }
        if (rightTableReference == null || rightTableReference.trim().isEmpty()) {
            throw new IllegalArgumentException("Right table reference cannot be null or empty");
        }
        if (rightTableReference.contains(".")) {
            throw new IllegalArgumentException(
                    "Right table reference must not contain dot: '" + rightTableReference + "'");
        }
        if (rightColumn == null || rightColumn.trim().isEmpty()) {
            throw new IllegalArgumentException("Right column cannot be null or empty");
        }
        if (rightColumn.contains(".")) {
            throw new IllegalArgumentException(
                    "Right column must not contain dot. Use on(table, column, table, column) with separate parameters");
        }

        ColumnReference leftColRef = ColumnReference.of(leftTableReference, leftColumn);
        ColumnReference rightColRef = ColumnReference.of(rightTableReference, rightColumn);

        FromSource right = rightTableAlias != null
                ? new TableIdentifier(rightTableName, rightTableAlias)
                : new TableIdentifier(rightTableName);

        JoinBuilder joinBuilder = new JoinBuilder(parent, left, joinType, right);
        return joinBuilder.on(leftColRef, rightColRef);
    }
}
