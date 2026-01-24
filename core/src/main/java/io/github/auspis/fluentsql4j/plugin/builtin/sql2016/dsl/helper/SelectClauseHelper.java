package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.dsl.helper;

import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.List;

/**
 * Helper class for SELECT statement clause validation and assembly. Provides methods to check
 * if optional clauses are present in a SELECT statement and to append them to a SQL builder.
 * Follows the helper class conventions from the project guidelines.
 */
public class SelectClauseHelper {

    private final SelectStatement selectStatement;

    public SelectClauseHelper(SelectStatement selectStatement) {
        this.selectStatement = selectStatement;
    }

    /**
     * Checks if the WHERE clause is present and has a condition.
     */
    public boolean hasWhereClause() {
        return selectStatement.getWhere() != null && selectStatement.getWhere().condition() != null;
    }

    /**
     * Checks if the GROUP BY clause is present and has grouping expressions.
     */
    public boolean hasGroupByClause() {
        return selectStatement.getGroupBy() != null
                && !selectStatement.getGroupBy().groupingExpressions().isEmpty();
    }

    /**
     * Checks if the HAVING clause is present and has a condition.
     */
    public boolean hasHavingClause() {
        return selectStatement.getHaving() != null
                && selectStatement.getHaving().condition() != null;
    }

    /**
     * Checks if the ORDER BY clause is present and has sorting expressions.
     */
    public boolean hasOrderByClause() {
        return selectStatement.getOrderBy() != null
                && !selectStatement.getOrderBy().sortings().isEmpty();
    }

    /**
     * Checks if the pagination (FETCH) clause is present and is active.
     */
    public boolean hasPaginationClause() {
        return selectStatement.getFetch() != null && selectStatement.getFetch().isActive();
    }

    /**
     * Appends an optional clause to the SQL builder if the clause spec is not null and not blank.
     *
     * @param sqlBuilder the StringBuilder to append to
     * @param parameters the list to collect parameters from the clause
     * @param spec the PreparedStatementSpec of the clause
     * @param keyword the SQL keyword to prepend (e.g., "WHERE", "GROUP BY", "ORDER BY")
     */
    public void appendOptionalClause(
            StringBuilder sqlBuilder, List<Object> parameters, PreparedStatementSpec spec, String keyword) {
        if (spec != null && !spec.sql().isBlank()) {
            sqlBuilder.append(" ").append(keyword).append(" ").append(spec.sql());
            parameters.addAll(spec.parameters());
        }
    }

    /**
     * Appends a clause without a keyword (used for FETCH/pagination which includes its own
     * formatting).
     *
     * @param sqlBuilder the StringBuilder to append to
     * @param parameters the list to collect parameters from the clause
     * @param spec the PreparedStatementSpec of the clause
     */
    public void appendClauseWithoutKeyword(
            StringBuilder sqlBuilder, List<Object> parameters, PreparedStatementSpec spec) {
        if (spec != null && !spec.sql().isBlank()) {
            sqlBuilder.append(" ").append(spec.sql());
            parameters.addAll(spec.parameters());
        }
    }
}
