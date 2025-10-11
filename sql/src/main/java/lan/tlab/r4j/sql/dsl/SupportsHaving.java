package lan.tlab.r4j.sql.dsl;

import java.util.function.Function;
import lan.tlab.r4j.sql.ast.clause.conditional.having.Having;
import lan.tlab.r4j.sql.ast.predicate.Predicate;

/**
 * Interface for builders that support HAVING clause construction.
 *
 * @param <T> the builder type that implements this interface
 */
public interface SupportsHaving<T> {

    /**
     * Updates the HAVING clause using the provided function.
     *
     * @param updater function to update the HAVING clause
     * @return the builder instance
     */
    T updateHaving(Function<Having, Having> updater);

    /**
     * Gets the table reference for column resolution.
     *
     * @return the table reference string
     */
    String getTableReference();

    /**
     * Adds a HAVING condition with the specified logical combinator.
     *
     * @param condition the predicate to add
     * @param combinator the logical combinator (AND/OR)
     * @return the builder instance
     */
    T addHavingCondition(Predicate condition, LogicalCombinator combinator);
}
