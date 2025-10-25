package lan.tlab.r4j.sql.dsl;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.dsl.delete.DeleteBuilder;
import lan.tlab.r4j.sql.dsl.insert.InsertBuilder;
import lan.tlab.r4j.sql.dsl.merge.MergeBuilder;
import lan.tlab.r4j.sql.dsl.select.SelectBuilder;
import lan.tlab.r4j.sql.dsl.select.SelectProjectionBuilder;
import lan.tlab.r4j.sql.dsl.table.CreateTableBuilder;
import lan.tlab.r4j.sql.dsl.update.UpdateBuilder;

public class DSL {

    private final DialectRenderer renderer;

    /**
     * Creates a DSL instance configured for a specific SQL dialect.
     * <p>
     * This constructor is used by {@link DSLRegistry} to create dialect-specific
     * DSL instances. It can also be used directly when you have a {@link DialectRenderer}
     * and want to create a DSL instance without going through the registry.
     * <p>
     * <b>Example usage via DSLRegistry (recommended):</b>
     * <pre>{@code
     * DSLRegistry registry = DSLRegistry.createWithServiceLoader();
     * DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();
     * String sql = dsl.select("name").from("users").build();
     * }</pre>
     * <p>
     * <b>Example direct usage:</b>
     * <pre>{@code
     * DialectRenderer renderer = ...; // obtain from SqlDialectPluginRegistry
     * DSL dsl = new DSL(renderer);
     * String sql = dsl.select("name").from("users").build();
     * }</pre>
     *
     * @param renderer the dialect renderer to use for this DSL instance
     * @throws NullPointerException if {@code renderer} is {@code null}
     * @see DSLRegistry
     */
    public DSL(DialectRenderer renderer) {
        this.renderer = java.util.Objects.requireNonNull(renderer, "DialectRenderer must not be null");
    }

    // Static methods that accept a DialectRenderer (for backward compatibility and explicit control)

    public static CreateTableBuilder createTable(DialectRenderer renderer, String tableName) {
        return new CreateTableBuilder(renderer, tableName);
    }

    public static SelectProjectionBuilder select(DialectRenderer renderer) {
        return new SelectProjectionBuilder(renderer);
    }

    public static SelectBuilder select(DialectRenderer renderer, String... columns) {
        return new SelectBuilder(renderer, columns);
    }

    public static SelectBuilder selectAll(DialectRenderer renderer) {
        return new SelectBuilder(renderer, "*");
    }

    public static InsertBuilder insertInto(DialectRenderer renderer, String tableName) {
        return new InsertBuilder(renderer, tableName);
    }

    public static DeleteBuilder deleteFrom(DialectRenderer renderer, String tableName) {
        return new DeleteBuilder(renderer, tableName);
    }

    public static UpdateBuilder update(DialectRenderer renderer, String tableName) {
        return new UpdateBuilder(renderer, tableName);
    }

    public static MergeBuilder mergeInto(DialectRenderer renderer, String targetTableName) {
        return new MergeBuilder(renderer, targetTableName);
    }

    // Instance methods using the configured renderer (new instance-based API)

    /**
     * Creates a CREATE TABLE builder using this DSL instance's configured renderer.
     *
     * @param tableName the name of the table to create
     * @return a new CreateTableBuilder instance
     */
    public CreateTableBuilder createTable(String tableName) {
        return createTable(renderer, tableName);
    }

    /**
     * Creates a SELECT builder with no columns specified, using this DSL instance's configured renderer.
     * <p>
     * Use this method when you want to build the projection dynamically.
     *
     * @return a new SelectProjectionBuilder instance
     */
    public SelectProjectionBuilder select() {
        return select(renderer);
    }

    /**
     * Creates a SELECT builder with the specified columns, using this DSL instance's configured renderer.
     *
     * @param columns the columns to select
     * @return a new SelectBuilder instance
     */
    public SelectBuilder select(String... columns) {
        return select(renderer, columns);
    }

    /**
     * Creates a SELECT * builder using this DSL instance's configured renderer.
     *
     * @return a new SelectBuilder instance configured to select all columns
     */
    public SelectBuilder selectAll() {
        return selectAll(renderer);
    }

    /**
     * Creates an INSERT INTO builder using this DSL instance's configured renderer.
     *
     * @param tableName the name of the table to insert into
     * @return a new InsertBuilder instance
     */
    public InsertBuilder insertInto(String tableName) {
        return insertInto(renderer, tableName);
    }

    /**
     * Creates a DELETE FROM builder using this DSL instance's configured renderer.
     *
     * @param tableName the name of the table to delete from
     * @return a new DeleteBuilder instance
     */
    public DeleteBuilder deleteFrom(String tableName) {
        return deleteFrom(renderer, tableName);
    }

    /**
     * Creates an UPDATE builder using this DSL instance's configured renderer.
     *
     * @param tableName the name of the table to update
     * @return a new UpdateBuilder instance
     */
    public UpdateBuilder update(String tableName) {
        return update(renderer, tableName);
    }

    /**
     * Creates a MERGE INTO builder using this DSL instance's configured renderer.
     *
     * @param targetTableName the name of the target table for the merge operation
     * @return a new MergeBuilder instance
     */
    public MergeBuilder mergeInto(String targetTableName) {
        return mergeInto(renderer, targetTableName);
    }
}
