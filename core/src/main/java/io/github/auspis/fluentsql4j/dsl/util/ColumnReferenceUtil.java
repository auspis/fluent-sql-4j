package io.github.auspis.fluentsql4j.dsl.util;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;

/**
 * Utility class for ColumnReference operations (retargeting, validation, normalization).
 *
 * <p>This utility provides methods for:
 * <ul>
 *   <li>Retargeting column references to new table contexts</li>
 *   <li>Checking if a column should be retargeted</li>
 *   <li>Detecting wildcard columns</li>
 * </ul>
 *
 * <p>This class cannot be instantiated.
 */
public final class ColumnReferenceUtil {

    private ColumnReferenceUtil() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Retarget a ColumnReference to a new table reference if appropriate.
     *
     * <p>A column reference should be retargeted if:
     * <ul>
     *   <li>It has no table reference (table is null or empty), OR</li>
     *   <li>Its table reference matches the current table name</li>
     * </ul>
     *
     * <p>Column references with explicit table qualifiers that don't match the current table
     * are left unchanged (e.g., columns from joined tables).
     *
     * @param colRef the original column reference
     * @param currentTableName the name of the current table (for matching)
     * @param newTableReference the new table reference (alias or name)
     * @return retargeted ColumnReference or original if not applicable
     */
    public static ColumnReference retargetIfApplicable(
            ColumnReference colRef, String currentTableName, String newTableReference) {
        if (shouldRetarget(colRef, currentTableName)) {
            return ColumnReference.of(newTableReference, colRef.column());
        }
        return colRef;
    }

    /**
     * Check if a ColumnReference should be retargeted to the current table.
     *
     * <p>A column should be retargeted if:
     * <ul>
     *   <li>It has no table reference (table is null or empty), OR</li>
     *   <li>Its table reference matches the current table name</li>
     * </ul>
     *
     * @param colRef the column reference to check
     * @param currentTableName the name of the current table
     * @return true if the column should be retargeted, false otherwise
     */
    public static boolean shouldRetarget(ColumnReference colRef, String currentTableName) {
        boolean hasTable = colRef.table() != null && !colRef.table().isEmpty();
        return !hasTable || colRef.table().equals(currentTableName);
    }

    /**
     * Check if a ColumnReference is a wildcard (*).
     *
     * @param colRef the column reference to check
     * @return true if the column is "*", false otherwise
     */
    public static boolean isWildcard(ColumnReference colRef) {
        return "*".equals(colRef.column());
    }

    /**
     * Create a ColumnReference with a table reference from a table reference string and column name.
     *
     * <p>This is a null-safe wrapper around {@link ColumnReference#of(String, String)} that
     * validates the table reference before creating the ColumnReference.
     *
     * @param tableReference the table reference (can be null or empty, will use empty string)
     * @param column the column name
     * @return a new ColumnReference with the table reference and column name
     * @throws IllegalArgumentException if column is null or empty
     */
    public static ColumnReference createWithTableReference(String tableReference, String column) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        String safeTableReference = tableReference != null ? tableReference : "";
        return ColumnReference.of(safeTableReference, column);
    }

    /**
     * Create and validate a ColumnReference with comprehensive validation for both table and column.
     *
     * <p>This method validates:
     * <ul>
     *   <li>Table reference is not null or empty</li>
     *   <li>Table reference does not contain dot notation</li>
     *   <li>Column name is not null or empty</li>
     *   <li>Column name does not contain dot notation</li>
     * </ul>
     *
     * <p>Use this method when both table and column must be explicitly validated,
     * typically for JOIN conditions or multi-table operations.
     *
     * @param table the table reference (alias or name)
     * @param column the column name
     * @return a new validated ColumnReference
     * @throws IllegalArgumentException if any validation fails
     */
    public static ColumnReference createValidated(String table, String column) {
        if (table == null || table.trim().isEmpty()) {
            throw new IllegalArgumentException("Table reference cannot be null or empty");
        }
        if (table.contains(".")) {
            throw new IllegalArgumentException("Table reference must not contain dot: '" + table + "'");
        }
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        if (column.contains(".")) {
            throw new IllegalArgumentException(
                    "Column name must not contain dot. Use createValidated(table, column) with separate parameters");
        }
        return ColumnReference.of(table, column);
    }

    /**
     * Create and validate a ColumnReference with validation for column only (no table reference).
     *
     * <p>This method validates:
     * <ul>
     *   <li>Column name is not null or empty</li>
     *   <li>Column name does not contain dot notation</li>
     * </ul>
     *
     * <p>The table reference is set to an empty string. Use this method when the column
     * should not be table-qualified, typically for single-table INSERT/UPDATE operations.
     *
     * @param column the column name
     * @return a new validated ColumnReference with empty table reference
     * @throws IllegalArgumentException if any validation fails
     */
    public static ColumnReference createValidated(String column) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        if (column.contains(".")) {
            throw new IllegalArgumentException("Column name must not contain dot notation: '" + column + "'");
        }
        return ColumnReference.of("", column);
    }
}
