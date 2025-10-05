package lan.tlab.r4j.sql.dsl.select;

import lan.tlab.r4j.sql.ast.clause.from.source.FromSource;
import lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;

public class JoinClauseBuilder {
    private final SelectBuilder parent;
    private final FromSource left;
    private final OnJoin.JoinType joinType;
    private final String rightTableName;
    private String rightTableAlias;

    public JoinClauseBuilder(SelectBuilder parent, FromSource left, OnJoin.JoinType joinType, String rightTableName) {
        this.parent = parent;
        this.left = left;
        this.joinType = joinType;
        this.rightTableName = rightTableName;
    }

    public JoinClauseBuilder as(String alias) {
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

        ColumnReference leftColRef = parseColumnReference(leftColumn);
        ColumnReference rightColRef = parseColumnReference(rightColumn);

        FromSource right = rightTableAlias != null
                ? new TableIdentifier(rightTableName, rightTableAlias)
                : new TableIdentifier(rightTableName);

        JoinBuilder joinBuilder = new JoinBuilder(parent, left, joinType, right);
        return joinBuilder.on(leftColRef, rightColRef);
    }

    private ColumnReference parseColumnReference(String column) {
        if (column.contains(".")) {
            String[] parts = column.split("\\.", 2);
            return ColumnReference.of(parts[0], parts[1]);
        }
        return ColumnReference.of("", column);
    }
}
