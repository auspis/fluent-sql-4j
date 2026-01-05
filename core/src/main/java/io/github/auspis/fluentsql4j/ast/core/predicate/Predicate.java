package io.github.auspis.fluentsql4j.ast.core.predicate;

import io.github.auspis.fluentsql4j.ast.core.expression.Expression;

/**
 * Represents a predicate that evaluates to a boolean value (TRUE/FALSE/UNKNOWN).
 * Used in WHERE, HAVING, ON clauses.
 */
public interface Predicate extends Expression {

    /*
    A predicate produces a TRUE/FALSE/UNKNOWN value.
       ComparisonPredicate: (E.g., =, >, <, IS NULL, LIKE, IN, BETWEEN)
           Accepts as component/parameter:
               A ComparisonOperator (e.g., RelationalComparisonOperator, LikeComparisonOperator, InComparisonOperator, BetweenComparisonOperator).
               One or more ScalarExpression as operands, whose number and role depend on the specific operator:
                   Binary operators (=, >): 2 ScalarExpression (left and right operand).
                   IS NULL/IS NOT NULL: 1 ScalarExpression.
                   IN: 1 ScalarExpression (value to compare) and a set of values (which can be a list of ScalarExpression or the result of a ColumnSubquery).
                   BETWEEN: 1 ScalarExpression (value to compare) and 2 ScalarExpression (lower and upper bound).
           Example: price > 100 (price and 100 are ScalarExpression; > is a RelationalComparisonOperator).
       LogicalPredicate: (E.g., AND, OR, NOT)
           Accepts as component/parameter:
               A LogicalOperator (AND, OR, NOT).
               One or more Predicate as operands.
                   AND/OR: Two or more Predicate.
                   NOT: A single Predicate.
           Example: (condition1 AND condition2) (condition1 and condition2 are Predicate; AND is a ConjunctiveLogicalOperator).
    */

    /*
    - Definition: Produce TRUE, FALSE, or UNKNOWN.
    - Where they can be used:
       	- SELECT (YES/NO - depends on DB): Some DBs (e.g., PostgreSQL, MySQL 8.0+) allow direct projection of predicates that return TRUE/FALSE (or 1/0). Others don't or require an explicit CASE.
           	Example (PostgreSQL): SELECT CustomerName, (Salary > 50000) AS HighEarner FROM Employees;
           	Example (Generally): SELECT CustomerName, CASE WHEN Salary > 50000 THEN 'Yes' ELSE 'No' END AS HighEarner FROM Employees;
    	- WHERE (YES): Their primary role. The WHERE clause expects a single predicate that determines which rows to include.
           	Example: WHERE OrderDate > '2023-01-01' AND CustomerID = 123
       	- HAVING (YES): Their primary role. The HAVING clause expects a single predicate that determines which groups to include.
           	Example: HAVING COUNT(*) > 10 OR SUM(Amount) > 10000
       	- GROUP BY (NO): Makes no sense. You don't group by a TRUE/FALSE/UNKNOWN boolean value that isn't directly tied to a column.
       	- ORDER BY (YES/NO - depends on DB): Similar to SELECT, some DBs accept them directly, others don't. If accepted, they order by TRUE/FALSE (often mapped to 1/0).
     */

}
