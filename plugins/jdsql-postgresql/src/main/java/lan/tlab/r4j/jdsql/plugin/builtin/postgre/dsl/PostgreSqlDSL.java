package lan.tlab.r4j.jdsql.plugin.builtin.postgre.dsl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.dsl.DSL;

/**
 * PostgreSQL-specific DSL extension providing access to PostgreSQL custom functions.
 * <p>
 * This class extends the base DSL with PostgreSQL-specific functions like:
 * <ul>
 *   <li>STRING_AGG - String aggregation with delimiter and ordering</li>
 *   <li>ARRAY_AGG - Array aggregation</li>
 *   <li>JSONB_AGG - JSONB array aggregation</li>
 *   <li>TO_CHAR - Format timestamp to string</li>
 *   <li>DATE_TRUNC - Truncate date to specified precision</li>
 *   <li>AGE - Calculate interval between dates</li>
 * </ul>
 */
public class PostgreSqlDSL extends DSL {

    public PostgreSqlDSL(PreparedStatementSpecFactory specFactory) {
        super(specFactory);
    }

    /**
     * Creates a STRING_AGG function builder.
     * <p>
     * STRING_AGG concatenates input values into a string with a specified delimiter.
     *
     * @param column the column name to aggregate
     * @return a builder for STRING_AGG options
     */
    public StringAggBuilder stringAgg(String column) {
        return new StringAggBuilder(column);
    }

    /**
     * Creates a STRING_AGG function builder with explicit table reference.
     *
     * @param table the table name
     * @param column the column name to aggregate
     * @return a builder for STRING_AGG options
     */
    public StringAggBuilder stringAgg(String table, String column) {
        return new StringAggBuilder(table, column);
    }

    /**
     * Creates an ARRAY_AGG function call.
     * <p>
     * ARRAY_AGG aggregates input values into an array.
     *
     * @param column the column to aggregate
     * @return a builder for ARRAY_AGG options
     */
    public ArrayAggBuilder arrayAgg(String column) {
        return new ArrayAggBuilder(column);
    }

    /**
     * Creates a JSONB_AGG function call.
     * <p>
     * JSONB_AGG aggregates values into a JSONB array.
     *
     * @param column the column to aggregate
     * @return a builder for JSONB_AGG options
     */
    public JsonbAggBuilder jsonbAgg(String column) {
        return new JsonbAggBuilder(column);
    }

    /**
     * Creates a TO_CHAR function call for date/time formatting.
     *
     * @param expression the timestamp expression to format
     * @param format the format pattern
     * @return a CustomFunctionCall representing TO_CHAR
     */
    public ScalarExpression toChar(ScalarExpression expression, String format) {
        return new CustomFunctionCall("TO_CHAR", List.of(expression, Literal.of(format)), Map.of());
    }

    /**
     * Creates a DATE_TRUNC function call.
     * <p>
     * Truncates a timestamp to the specified precision.
     *
     * @param field the precision field
     * @param expression the timestamp expression
     * @return a CustomFunctionCall representing DATE_TRUNC
     */
    public ScalarExpression dateTrunc(String field, ScalarExpression expression) {
        return new CustomFunctionCall("DATE_TRUNC", List.of(Literal.of(field), expression), Map.of());
    }

    /**
     * Creates an AGE function call to calculate interval.
     * <p>
     * When called with one argument, calculates age from current date.
     * When called with two arguments, calculates interval between them.
     *
     * @param expressions one or two timestamp expressions
     * @return a CustomFunctionCall representing AGE
     */
    public ScalarExpression age(ScalarExpression... expressions) {
        if (expressions.length == 0 || expressions.length > 2) {
            throw new IllegalArgumentException("AGE requires 1 or 2 arguments");
        }
        return new CustomFunctionCall("AGE", List.of(expressions), Map.of());
    }

    /**
     * Creates a COALESCE function call.
     * <p>
     * Returns the first non-null expression.
     *
     * @param expressions the expressions to evaluate
     * @return a CustomFunctionCall representing COALESCE
     */
    public ScalarExpression coalesce(ScalarExpression... expressions) {
        if (expressions.length < 2) {
            throw new IllegalArgumentException("COALESCE requires at least 2 arguments");
        }
        return new CustomFunctionCall("COALESCE", List.of(expressions), Map.of());
    }

    /**
     * Creates a NULLIF function call.
     * <p>
     * Returns NULL if the two expressions are equal, otherwise returns the first expression.
     *
     * @param expr1 the first expression
     * @param expr2 the second expression
     * @return a CustomFunctionCall representing NULLIF
     */
    public ScalarExpression nullIf(ScalarExpression expr1, ScalarExpression expr2) {
        return new CustomFunctionCall("NULLIF", List.of(expr1, expr2), Map.of());
    }

    /**
     * Builder for STRING_AGG function with all its options.
     */
    public class StringAggBuilder {
        private final ColumnReference column;
        private String orderBy;
        private String separator = ",";
        private boolean distinct = false;

        StringAggBuilder(String column) {
            this.column = ColumnReference.of("", column);
        }

        StringAggBuilder(String table, String column) {
            this.column = ColumnReference.of(table, column);
        }

        /**
         * Adds ORDER BY clause to STRING_AGG.
         *
         * @param orderBy the order by expression
         * @return this builder
         */
        public StringAggBuilder orderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        /**
         * Sets the separator for STRING_AGG.
         *
         * @param separator the separator string
         * @return this builder
         */
        public StringAggBuilder separator(String separator) {
            this.separator = separator;
            return this;
        }

        /**
         * Adds DISTINCT to STRING_AGG.
         *
         * @return this builder
         */
        public StringAggBuilder distinct() {
            this.distinct = true;
            return this;
        }

        /**
         * Builds the STRING_AGG CustomFunctionCall.
         *
         * @return a CustomFunctionCall representing STRING_AGG
         */
        public ScalarExpression build() {
            Map<String, Object> options = new HashMap<>();
            if (orderBy != null) {
                options.put("ORDER_BY", orderBy);
            }
            options.put("SEPARATOR", separator);
            options.put("DISTINCT", distinct);

            return new CustomFunctionCall("STRING_AGG", List.of(column), options);
        }
    }

    /**
     * Builder for ARRAY_AGG function.
     */
    public class ArrayAggBuilder {
        private final ColumnReference column;
        private String orderBy;
        private boolean distinct = false;

        ArrayAggBuilder(String column) {
            this.column = ColumnReference.of("", column);
        }

        public ArrayAggBuilder orderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public ArrayAggBuilder distinct() {
            this.distinct = true;
            return this;
        }

        public ScalarExpression build() {
            Map<String, Object> options = new HashMap<>();
            if (orderBy != null) {
                options.put("ORDER_BY", orderBy);
            }
            options.put("DISTINCT", distinct);

            return new CustomFunctionCall("ARRAY_AGG", List.of(column), options);
        }
    }

    /**
     * Builder for JSONB_AGG function.
     */
    public class JsonbAggBuilder {
        private final ColumnReference column;
        private String orderBy;

        JsonbAggBuilder(String column) {
            this.column = ColumnReference.of("", column);
        }

        public JsonbAggBuilder orderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public ScalarExpression build() {
            Map<String, Object> options = new HashMap<>();
            if (orderBy != null) {
                options.put("ORDER_BY", orderBy);
            }

            return new CustomFunctionCall("JSONB_AGG", List.of(column), options);
        }
    }
}
