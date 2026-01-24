package io.github.auspis.fluentsql4j.dsl.clause;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.dsl.select.SelectBuilder;
import io.github.auspis.fluentsql4j.dsl.util.ColumnReferenceUtil;

/**
 * Builder for HAVING clause that supports column conditions.
 * <p>
 * This builder provides a fluent API for constructing HAVING predicates, including:
 * <ul>
 *   <li>Column comparisons via {@link HavingConditionBuilder}</li>
 * </ul>
 * <p>
 * Example usage:
 * <pre>{@code
 * dsl.select("category", "COUNT(*)")
 *     .from("products")
 *     .groupBy("category")
 *     .having()
 *     .column("COUNT(*)").gt(5)
 *     .and()
 *     .column("AVG(price)").gte(100)
 *     .build();
 * }</pre>
 */
public class HavingBuilder {

    private final SelectBuilder parent;
    private final LogicalCombinator combinator;

    public HavingBuilder(SelectBuilder parent, LogicalCombinator combinator) {
        this.parent = parent;
        this.combinator = combinator;
    }

    /**
     * Start a condition on a column or aggregate function using the parent's table reference.
     * <p>
     * This method is suitable for single-table queries or when referencing aggregate functions.
     *
     * @param column the column name or aggregate function expression (must not contain dot notation)
     * @return a condition builder for the column
     * @throws IllegalArgumentException if column is null, empty, or contains dot notation
     */
    public HavingConditionBuilder column(String column) {
        ColumnReference colRef =
                ColumnReferenceUtil.createValidatedWithTrustedTable(parent.getTableReference(), column);
        return new HavingConditionBuilder(parent, colRef, combinator);
    }

    /**
     * Start a condition on a column with explicit table alias.
     * <p>
     * This method is suitable for multi-table queries (e.g., with JOINs) where you need to
     * reference columns from specific tables in HAVING conditions.
     * <p>
     * Example:
     * <pre>{@code
     * .having()
     * .column("o", "total").gt(100)
     * .and()
     * .column("c", "count").gte(5)
     * }</pre>
     *
     * @param alias the table alias (must not contain dot notation)
     * @param column the column name (must not contain dot notation)
     * @return a condition builder for the column
     * @throws IllegalArgumentException if alias or column is null, empty, or contains dot notation
     */
    public HavingConditionBuilder column(String alias, String column) {
        ColumnReference colRef = ColumnReferenceUtil.createValidated(alias, column);
        return new HavingConditionBuilder(parent, colRef, combinator);
    }
}
