package io.github.auspis.fluentsql4j.ast.dql.clause;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import io.github.auspis.fluentsql4j.ast.core.clause.Clause;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

/**
 * Represents a GROUP BY clause in a SELECT statement.
 *
 * <p>GROUP BY accepts only {@link ScalarExpression scalar expressions} (column references,
 * literals, arithmetic expressions, functions, etc.) to define the grouping key.
 *
 * <p>This type safety prevents aggregate expressions (COUNT, SUM, AVG, etc.) from being used
 * in GROUP BY, which would generate invalid SQL like {@code GROUP BY SUM(amount)}.
 *
 * <p>Examples:
 * <ul>
 *   <li>{@code GROUP BY department} - column reference
 *   <li>{@code GROUP BY YEAR(order_date)} - function call
 *   <li>{@code GROUP BY department, region} - multiple columns
 * </ul>
 *
 * <p>Invalid (prevented by type system):
 * <ul>
 *   <li>{@code GROUP BY SUM(amount)} - ❌ aggregate expression not allowed
 *   <li>{@code GROUP BY COUNT(*)} - ❌ aggregate expression not allowed
 * </ul>
 */
public record GroupBy(List<ScalarExpression> groupingExpressions) implements Clause {

    public GroupBy {
        if (groupingExpressions == null) {
            groupingExpressions = Collections.unmodifiableList(new ArrayList<>());
        }
    }

    public static GroupBy nullObject() {
        return new GroupBy(null);
    }

    public static GroupBy of(ScalarExpression... expressions) {
        return of(Stream.of(expressions).toList());
    }

    public static GroupBy of(List<ScalarExpression> expressions) {
        return new GroupBy(expressions);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
