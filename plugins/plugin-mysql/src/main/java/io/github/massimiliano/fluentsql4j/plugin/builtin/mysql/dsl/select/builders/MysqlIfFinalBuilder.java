package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.dsl.select.builders;

import io.github.auspis.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.PredicateExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.core.predicate.Predicate;
import io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.dsl.MysqlDSL;
import io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.dsl.select.MysqlSelectProjectionBuilder;
import java.util.List;
import java.util.Map;

/**
 * Final builder for MySQL IF function that completes the expression
 * and returns to the parent projection builder.
 */
public class MysqlIfFinalBuilder {

    private final MysqlSelectProjectionBuilder parent;
    private final Predicate condition;
    private final ScalarExpression trueValue;

    public MysqlIfFinalBuilder(
            MysqlSelectProjectionBuilder parent, MysqlDSL dsl, Predicate condition, ScalarExpression trueValue) {
        this.parent = parent;
        this.condition = condition;
        this.trueValue = trueValue;
    }

    /**
     * Specifies the value to return when the condition is false,
     * completes the IF function, and prepares for alias assignment.
     *
     * @param value the value for the false case
     * @return a projection alias builder
     */
    public MysqlIfProjectionAliasBuilder otherwise(Object value) {
        java.util.Objects.requireNonNull(value, "Otherwise value must not be null");
        ScalarExpression falseValue = Literal.of(String.valueOf(value));

        // Wrap the Predicate in PredicateExpression to use it as ScalarExpression
        PredicateExpression predicateExpr = new PredicateExpression(condition);

        CustomFunctionCall ifExpression =
                new CustomFunctionCall("IF", List.of(predicateExpr, trueValue, falseValue), Map.of());

        return new MysqlIfProjectionAliasBuilder(parent, ifExpression);
    }

    /**
     * Inner builder to handle the final alias assignment and return to parent.
     */
    public static class MysqlIfProjectionAliasBuilder {
        private final MysqlSelectProjectionBuilder parent;
        private final CustomFunctionCall ifExpression;

        public MysqlIfProjectionAliasBuilder(MysqlSelectProjectionBuilder parent, CustomFunctionCall ifExpression) {
            this.parent = parent;
            this.ifExpression = ifExpression;
        }

        /**
         * Assigns an alias to the IF function result and returns to the parent builder.
         *
         * @param alias the column alias
         * @return the parent MysqlSelectProjectionBuilder for method chaining
         */
        public MysqlSelectProjectionBuilder as(String alias) {
            return parent.expression(ifExpression, alias);
        }
    }
}
