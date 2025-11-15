package lan.tlab.r4j.jdsql.dsl.select;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.BehaviorKind;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonExists;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonQuery;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonValue;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.OnEmptyBehavior;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.WrapperBehavior;

/**
 * Fluent builder for JSON functions in SELECT projections.
 * <p>
 * This builder provides a fluent API for constructing JSON function calls
 * (JSON_EXISTS, JSON_VALUE, JSON_QUERY) with optional configurations like
 * return types, behaviors, and wrappers.
 * <p>
 * Example usage:
 * <pre>{@code
 * dsl.select()
 *     .column("name")
 *     .jsonExists("data", "$.email").as("has_email")
 *     .jsonValue("data", "$.price").returning("DECIMAL(10,2)").as("price")
 *     .jsonQuery("data", "$.items").withWrapper().as("items")
 *     .from("products")
 *     .build();
 * }</pre>
 *
 * @param <PARENT> the type of the parent projection builder
 */
public class JsonFunctionBuilder<PARENT extends SelectProjectionBuilder<PARENT>> {

    private final PARENT parent;
    private final ColumnReference jsonDocument;
    private final String path;
    private final JsonFunctionType functionType;

    // Configuration options
    private String returningType;
    private WrapperBehavior wrapperBehavior;
    private OnEmptyBehavior onEmptyBehavior;
    private BehaviorKind onErrorBehavior;

    enum JsonFunctionType {
        EXISTS,
        VALUE,
        QUERY
    }

    JsonFunctionBuilder(PARENT parent, String table, String column, String path, JsonFunctionType functionType) {
        this.parent = parent;
        this.jsonDocument = ColumnReference.of(table, column);
        this.path = path;
        this.functionType = functionType;
    }

    /**
     * Specifies the return type for JSON_VALUE or JSON_QUERY functions.
     *
     * @param type the SQL data type (e.g., "DECIMAL(10,2)", "VARCHAR(100)", "JSON")
     * @return this builder for method chaining
     */
    public JsonFunctionBuilder<PARENT> returning(String type) {
        this.returningType = type;
        return this;
    }

    /**
     * Specifies wrapper behavior for JSON_QUERY function.
     * Equivalent to WITH WRAPPER clause.
     *
     * @return this builder for method chaining
     */
    public JsonFunctionBuilder<PARENT> withWrapper() {
        this.wrapperBehavior = WrapperBehavior.WITH_WRAPPER;
        return this;
    }

    /**
     * Specifies conditional wrapper behavior for JSON_QUERY function.
     * Equivalent to WITH CONDITIONAL WRAPPER clause.
     *
     * @return this builder for method chaining
     */
    public JsonFunctionBuilder<PARENT> withConditionalWrapper() {
        this.wrapperBehavior = WrapperBehavior.WITH_CONDITIONAL_WRAPPER;
        return this;
    }

    /**
     * Specifies no wrapper for JSON_QUERY function.
     * Equivalent to WITHOUT WRAPPER clause.
     *
     * @return this builder for method chaining
     */
    public JsonFunctionBuilder<PARENT> withoutWrapper() {
        this.wrapperBehavior = WrapperBehavior.WITHOUT_WRAPPER;
        return this;
    }

    /**
     * Specifies a default value to return when the path is empty.
     * Equivalent to DEFAULT 'value' ON EMPTY clause.
     *
     * @param defaultValue the default value as a string
     * @return this builder for method chaining
     */
    public JsonFunctionBuilder<PARENT> defaultOnEmpty(String defaultValue) {
        this.onEmptyBehavior = OnEmptyBehavior.defaultValue(defaultValue);
        return this;
    }

    /**
     * Specifies that NULL should be returned when the path is empty.
     * Equivalent to NULL ON EMPTY clause.
     *
     * @return this builder for method chaining
     */
    public JsonFunctionBuilder<PARENT> nullOnEmpty() {
        this.onEmptyBehavior = OnEmptyBehavior.returnNull();
        return this;
    }

    /**
     * Specifies that an error should be raised when the path is empty.
     * Equivalent to ERROR ON EMPTY clause.
     *
     * @return this builder for method chaining
     */
    public JsonFunctionBuilder<PARENT> errorOnEmpty() {
        this.onEmptyBehavior = OnEmptyBehavior.error();
        return this;
    }

    /**
     * Specifies that NULL should be returned on error.
     * Equivalent to NULL ON ERROR clause.
     *
     * @return this builder for method chaining
     */
    public JsonFunctionBuilder<PARENT> nullOnError() {
        this.onErrorBehavior = BehaviorKind.NONE;
        return this;
    }

    /**
     * Specifies that an error should be raised on error.
     * Equivalent to ERROR ON ERROR clause.
     *
     * @return this builder for method chaining
     */
    public JsonFunctionBuilder<PARENT> errorOnError() {
        this.onErrorBehavior = BehaviorKind.ERROR;
        return this;
    }

    /**
     * Finalizes the JSON function and sets an alias for it.
     *
     * @param alias the alias for this projection
     * @return the parent SelectProjectionBuilder for continued fluent API
     */
    public PARENT as(String alias) {
        switch (functionType) {
            case EXISTS -> {
                JsonExists jsonExists = new JsonExists(jsonDocument, Literal.of(path), onErrorBehavior);
                return parent.expression(jsonExists, alias);
            }
            case VALUE -> {
                JsonValue jsonValue =
                        new JsonValue(jsonDocument, Literal.of(path), returningType, onEmptyBehavior, onErrorBehavior);
                return parent.expression(jsonValue, alias);
            }
            case QUERY -> {
                JsonQuery jsonQuery = new JsonQuery(
                        jsonDocument,
                        Literal.of(path),
                        returningType,
                        wrapperBehavior,
                        onEmptyBehavior,
                        onErrorBehavior);
                return parent.expression(jsonQuery, alias);
            }
            default -> throw new IllegalStateException("Unknown JSON function type: " + functionType);
        }
    }
}
