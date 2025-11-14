package lan.tlab.r4j.sql.dsl;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.BehaviorKind;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.JsonExists;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.JsonQuery;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.JsonValue;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.OnEmptyBehavior;
import lan.tlab.r4j.sql.ast.common.predicate.Comparison;
import lan.tlab.r4j.sql.ast.common.predicate.IsNotNull;
import lan.tlab.r4j.sql.ast.common.predicate.IsNull;

/**
 * Fluent builder for JSON functions in WHERE conditions.
 * <p>
 * This builder provides a fluent API for constructing JSON function calls
 * (JSON_EXISTS, JSON_VALUE, JSON_QUERY) that can be used in WHERE predicates.
 * <p>
 * Example usage:
 * <pre>{@code
 * dsl.select("*")
 *     .from("users")
 *     .where()
 *     .jsonValue("info", "$.city").eq("Rome")
 *     .and()
 *     .jsonExists("info", "$.email")
 *     .build();
 * }</pre>
 */
public class WhereJsonFunctionBuilder<T extends SupportsWhere<T>> {

    private final T parent;
    private final ColumnReference jsonDocument;
    private final String path;
    private final JsonFunctionType functionType;
    private final LogicalCombinator combinator;

    // Configuration options
    private String returningType;
    private OnEmptyBehavior onEmptyBehavior;
    private BehaviorKind onErrorBehavior;

    enum JsonFunctionType {
        EXISTS,
        VALUE,
        QUERY
    }

    WhereJsonFunctionBuilder(
            T parent,
            String table,
            String column,
            String path,
            JsonFunctionType functionType,
            LogicalCombinator combinator) {
        this.parent = parent;
        // Use parent's table reference if table is null or empty
        String tableRef = (table == null || table.isEmpty()) ? parent.getTableReference() : table;
        this.jsonDocument = ColumnReference.of(tableRef, column);
        this.path = path;
        this.functionType = functionType;
        this.combinator = combinator;
    }

    /**
     * Specifies the return type for JSON_VALUE or JSON_QUERY functions.
     *
     * @param type the SQL data type (e.g., "DECIMAL(10,2)", "VARCHAR(100)")
     * @return this builder for method chaining
     */
    public WhereJsonFunctionBuilder<T> returning(String type) {
        this.returningType = type;
        return this;
    }

    /**
     * Specifies behavior when the JSON path returns empty result.
     *
     * @param behavior the empty behavior
     * @return this builder for method chaining
     */
    public WhereJsonFunctionBuilder<T> onEmpty(OnEmptyBehavior behavior) {
        this.onEmptyBehavior = behavior;
        return this;
    }

    /**
     * Specifies behavior when an error occurs during JSON processing.
     *
     * @param behavior the error behavior
     * @return this builder for method chaining
     */
    public WhereJsonFunctionBuilder<T> onError(BehaviorKind behavior) {
        this.onErrorBehavior = behavior;
        return this;
    }

    /**
     * For JSON_EXISTS: Checks if the JSON path exists (implicitly true).
     * Useful for predicates like: WHERE JSON_EXISTS(col, path)
     *
     * @return the parent builder
     */
    public T exists() {
        if (functionType != JsonFunctionType.EXISTS) {
            throw new IllegalStateException("exists() can only be called on JSON_EXISTS functions");
        }
        ScalarExpression jsonExistsExpr = buildJsonExists();
        return parent.addWhereCondition(Comparison.eq(jsonExistsExpr, Literal.of(true)), combinator);
    }

    /**
     * For JSON_EXISTS: Checks if the JSON path does not exist.
     *
     * @return the parent builder
     */
    public T notExists() {
        if (functionType != JsonFunctionType.EXISTS) {
            throw new IllegalStateException("notExists() can only be called on JSON_EXISTS functions");
        }
        ScalarExpression jsonExistsExpr = buildJsonExists();
        return parent.addWhereCondition(Comparison.eq(jsonExistsExpr, Literal.of(false)), combinator);
    }

    // Comparison methods for JSON_VALUE and JSON_QUERY

    public T eq(String value) {
        return addCondition(Comparison.eq(buildJsonFunction(), Literal.of(value)));
    }

    public T ne(String value) {
        return addCondition(Comparison.ne(buildJsonFunction(), Literal.of(value)));
    }

    public T gt(String value) {
        return addCondition(Comparison.gt(buildJsonFunction(), Literal.of(value)));
    }

    public T lt(String value) {
        return addCondition(Comparison.lt(buildJsonFunction(), Literal.of(value)));
    }

    public T gte(String value) {
        return addCondition(Comparison.gte(buildJsonFunction(), Literal.of(value)));
    }

    public T lte(String value) {
        return addCondition(Comparison.lte(buildJsonFunction(), Literal.of(value)));
    }

    public T eq(Number value) {
        return addCondition(Comparison.eq(buildJsonFunction(), Literal.of(value)));
    }

    public T ne(Number value) {
        return addCondition(Comparison.ne(buildJsonFunction(), Literal.of(value)));
    }

    public T gt(Number value) {
        return addCondition(Comparison.gt(buildJsonFunction(), Literal.of(value)));
    }

    public T lt(Number value) {
        return addCondition(Comparison.lt(buildJsonFunction(), Literal.of(value)));
    }

    public T gte(Number value) {
        return addCondition(Comparison.gte(buildJsonFunction(), Literal.of(value)));
    }

    public T lte(Number value) {
        return addCondition(Comparison.lte(buildJsonFunction(), Literal.of(value)));
    }

    public T isNull() {
        return addCondition(new IsNull(buildJsonFunction()));
    }

    public T isNotNull() {
        return addCondition(new IsNotNull(buildJsonFunction()));
    }

    private ScalarExpression buildJsonFunction() {
        return switch (functionType) {
            case EXISTS -> buildJsonExists();
            case VALUE -> buildJsonValue();
            case QUERY -> buildJsonQuery();
        };
    }

    private JsonExists buildJsonExists() {
        return new JsonExists(jsonDocument, Literal.of(path), onErrorBehavior);
    }

    private JsonValue buildJsonValue() {
        return new JsonValue(jsonDocument, Literal.of(path), returningType, onEmptyBehavior, onErrorBehavior);
    }

    private JsonQuery buildJsonQuery() {
        return new JsonQuery(jsonDocument, Literal.of(path), returningType, null, onEmptyBehavior, onErrorBehavior);
    }

    private T addCondition(lan.tlab.r4j.sql.ast.common.predicate.Predicate condition) {
        return parent.addWhereCondition(condition, combinator);
    }
}
