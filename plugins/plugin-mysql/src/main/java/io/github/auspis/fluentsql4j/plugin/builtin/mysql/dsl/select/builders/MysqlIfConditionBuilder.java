package io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.select.builders;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.predicate.Comparison;
import io.github.auspis.fluentsql4j.ast.core.predicate.IsNotNull;
import io.github.auspis.fluentsql4j.ast.core.predicate.IsNull;
import io.github.auspis.fluentsql4j.ast.core.predicate.Predicate;
import io.github.auspis.fluentsql4j.dsl.util.ColumnReferenceUtil;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.MysqlDSL;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.select.MysqlSelectProjectionBuilder;

/**
 * Builder for specifying the comparison condition in MySQL IF function.
 * Provides methods for various comparison operators.
 */
public class MysqlIfConditionBuilder {

    private final MysqlSelectProjectionBuilder parent;
    private final MysqlDSL dsl;
    private final ColumnReference column;

    public MysqlIfConditionBuilder(MysqlSelectProjectionBuilder parent, MysqlDSL dsl, String table, String column) {
        this.parent = parent;
        this.dsl = dsl;
        java.util.Objects.requireNonNull(column, "Column must not be null");
        this.column = ColumnReferenceUtil.createWithTableReference(table, column);
    }

    /**
     * Tests if column is greater than or equal to the specified value.
     *
     * @param value the value to compare against
     * @return builder for specifying the true-case value
     */
    public MysqlIfThenBuilder gte(Object value) {
        Predicate condition = Comparison.gte(column, Literal.of(String.valueOf(value)));
        return new MysqlIfThenBuilder(parent, dsl, condition);
    }

    /**
     * Tests if column is greater than the specified value.
     *
     * @param value the value to compare against
     * @return builder for specifying the true-case value
     */
    public MysqlIfThenBuilder gt(Object value) {
        Predicate condition = Comparison.gt(column, Literal.of(String.valueOf(value)));
        return new MysqlIfThenBuilder(parent, dsl, condition);
    }

    /**
     * Tests if column is less than or equal to the specified value.
     *
     * @param value the value to compare against
     * @return builder for specifying the true-case value
     */
    public MysqlIfThenBuilder lte(Object value) {
        Predicate condition = Comparison.lte(column, Literal.of(String.valueOf(value)));
        return new MysqlIfThenBuilder(parent, dsl, condition);
    }

    /**
     * Tests if column is less than the specified value.
     *
     * @param value the value to compare against
     * @return builder for specifying the true-case value
     */
    public MysqlIfThenBuilder lt(Object value) {
        Predicate condition = Comparison.lt(column, Literal.of(String.valueOf(value)));
        return new MysqlIfThenBuilder(parent, dsl, condition);
    }

    /**
     * Tests if column equals the specified value.
     *
     * @param value the value to compare against
     * @return builder for specifying the true-case value
     */
    public MysqlIfThenBuilder eq(Object value) {
        Predicate condition = Comparison.eq(column, Literal.of(String.valueOf(value)));
        return new MysqlIfThenBuilder(parent, dsl, condition);
    }

    /**
     * Tests if column is not equal to the specified value.
     *
     * @param value the value to compare against
     * @return builder for specifying the true-case value
     */
    public MysqlIfThenBuilder neq(Object value) {
        Predicate condition = Comparison.ne(column, Literal.of(String.valueOf(value)));
        return new MysqlIfThenBuilder(parent, dsl, condition);
    }

    /**
     * Tests if column is NULL.
     *
     * @return builder for specifying the true-case value
     */
    public MysqlIfThenBuilder isNull() {
        Predicate condition = new IsNull(column);
        return new MysqlIfThenBuilder(parent, dsl, condition);
    }

    /**
     * Tests if column is NOT NULL.
     *
     * @return builder for specifying the true-case value
     */
    public MysqlIfThenBuilder isNotNull() {
        Predicate condition = new IsNotNull(column);
        return new MysqlIfThenBuilder(parent, dsl, condition);
    }
}
