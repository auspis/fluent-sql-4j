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
}
