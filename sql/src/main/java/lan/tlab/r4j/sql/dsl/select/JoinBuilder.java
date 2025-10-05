package lan.tlab.r4j.sql.dsl.select;

import lan.tlab.r4j.sql.ast.clause.from.source.FromSource;
import lan.tlab.r4j.sql.ast.clause.from.source.join.OnJoin;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.predicate.Predicate;

public class JoinBuilder {
    private final SelectBuilder parent;
    private final FromSource left;
    private final OnJoin.JoinType joinType;
    private final String rightTableName;
    private String rightTableAlias;

    public JoinBuilder(SelectBuilder parent, FromSource left, OnJoin.JoinType joinType, String rightTableName) {
        this.parent = parent;
        this.left = left;
        this.joinType = joinType;
        this.rightTableName = rightTableName;
    }

    public JoinBuilder as(String alias) {
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

        String[] leftParts = parseColumnReference(leftColumn);
        String[] rightParts = parseColumnReference(rightColumn);

        ColumnReference leftColRef = ColumnReference.of(leftParts[0], leftParts[1]);
        ColumnReference rightColRef = ColumnReference.of(rightParts[0], rightParts[1]);

        Predicate onCondition = Comparison.eq(leftColRef, rightColRef);

        FromSource right = rightTableAlias != null
                ? new TableIdentifier(rightTableName, rightTableAlias)
                : new TableIdentifier(rightTableName);

        OnJoin join = new OnJoin(left, joinType, right, onCondition);

        return parent.addJoin(join);
    }

    private String[] parseColumnReference(String column) {
        if (column.contains(".")) {
            String[] parts = column.split("\\.", 2);
            return new String[] {parts[0], parts[1]};
        }
        return new String[] {"", column};
    }
}
