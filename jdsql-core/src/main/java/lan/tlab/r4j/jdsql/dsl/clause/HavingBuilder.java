package lan.tlab.r4j.jdsql.dsl.clause;

import lan.tlab.r4j.jdsql.dsl.select.SelectBuilder;

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
     * Start a condition on a column or aggregate function.
     *
     * @param column the column name or aggregate function expression
     * @return a condition builder for the column
     */
    public HavingConditionBuilder column(String column) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        return new HavingConditionBuilder(parent, column, combinator);
    }
}
