package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.dsl.select.builders;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.core.predicate.Predicate;
import io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.dsl.MysqlDSL;
import io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.dsl.select.MysqlSelectProjectionBuilder;

/**
 * Builder for specifying the true-case value in MySQL IF function.
 */
public class MysqlIfThenBuilder {

    private final MysqlSelectProjectionBuilder parent;
    private final MysqlDSL dsl;
    private final Predicate condition;

    public MysqlIfThenBuilder(MysqlSelectProjectionBuilder parent, MysqlDSL dsl, Predicate condition) {
        this.parent = parent;
        this.dsl = dsl;
        this.condition = condition;
    }

    /**
     * Specifies the value to return when the condition is true.
     *
     * @param value the value for the true case
     * @return builder for specifying the false-case value
     */
    public MysqlIfFinalBuilder then(Object value) {
        java.util.Objects.requireNonNull(value, "Then value must not be null");
        ScalarExpression trueValue = Literal.of(String.valueOf(value));
        return new MysqlIfFinalBuilder(parent, dsl, condition, trueValue);
    }
}
