package lan.tlab.r4j.jdsql.dsl.util;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;

public final class ColumnReferenceUtil {

    private ColumnReferenceUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Parses a column reference string into a ColumnReference object.
     * If the column contains a dot (.), it's treated as a qualified column name (table.column).
     * Otherwise, the provided table reference is used.
     *
     * @param column the column name, optionally qualified with table name (e.g., "table.column")
     * @param defaultTableReference the default table reference to use if column is not qualified
     * @return a ColumnReference object
     */
    public static ColumnReference parseColumnReference(String column, String defaultTableReference) {
        if (column.contains(".")) {
            String[] parts = column.split("\\.", 2);
            return ColumnReference.of(parts[0], parts[1]);
        }
        return ColumnReference.of(defaultTableReference, column);
    }
}
