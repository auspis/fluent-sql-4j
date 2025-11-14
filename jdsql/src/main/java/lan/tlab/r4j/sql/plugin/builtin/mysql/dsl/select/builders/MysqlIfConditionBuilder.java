package lan.tlab.r4j.sql.plugin.builtin.mysql.dsl.select.builders;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.predicate.Comparison;
import lan.tlab.r4j.sql.ast.common.predicate.IsNotNull;
import lan.tlab.r4j.sql.ast.common.predicate.IsNull;
import lan.tlab.r4j.sql.ast.common.predicate.Predicate;
import lan.tlab.r4j.sql.plugin.builtin.mysql.dsl.MysqlDSL;
import lan.tlab.r4j.sql.plugin.builtin.mysql.dsl.select.MysqlSelectProjectionBuilder;

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
        this.column = table != null ? ColumnReference.of(table, column) : ColumnReference.of("", column);
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
