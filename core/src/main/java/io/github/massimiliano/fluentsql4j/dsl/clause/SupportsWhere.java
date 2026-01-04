package io.github.massimiliano.fluentsql4j.dsl.clause;

import io.github.massimiliano.fluentsql4j.ast.core.predicate.Predicate;
import io.github.massimiliano.fluentsql4j.ast.dql.clause.Where;
import java.util.function.Function;

/**
 * Interface for builders that support WHERE clause construction.
 *
 * @param <T> the builder type that implements this interface
 */
public interface SupportsWhere<T> {

    /**
     * Updates the WHERE clause using the provided function.
     *
     * @param updater function to update the WHERE clause
     * @return the builder instance
     */
    T updateWhere(Function<Where, Where> updater);

    /**
     * Gets the table reference for column resolution.
     *
     * @return the table reference string
     */
    String getTableReference();

    /**
     * Adds a WHERE condition with the specified logical combinator.
     *
     * @param condition the predicate to add
     * @param combinator the logical combinator (AND/OR)
     * @return the builder instance
     */
    T addWhereCondition(Predicate condition, LogicalCombinator combinator);
}
