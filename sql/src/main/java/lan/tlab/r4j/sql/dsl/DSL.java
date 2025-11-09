package lan.tlab.r4j.sql.dsl;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.dsl.delete.DeleteBuilder;
import lan.tlab.r4j.sql.dsl.insert.InsertBuilder;
import lan.tlab.r4j.sql.dsl.merge.MergeBuilder;
import lan.tlab.r4j.sql.dsl.select.SelectBuilder;
import lan.tlab.r4j.sql.dsl.select.SelectProjectionBuilder;
import lan.tlab.r4j.sql.dsl.table.CreateTableBuilder;
import lan.tlab.r4j.sql.dsl.update.UpdateBuilder;

/**
 * Base DSL class for building SQL queries in a type-safe, fluent manner.
 * <p>
 * This class serves as the foundation for dialect-specific DSL implementations.
 * Subclasses can extend this class to add dialect-specific functionality, such as
 * custom SQL functions (e.g., MySQL's GROUP_CONCAT, IF, DATE_FORMAT).
 * <p>
 */
public class DSL {

    protected final DialectRenderer renderer;

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

    public CreateTableBuilder createTable(String tableName) {
        return new CreateTableBuilder(renderer, tableName);
    }

    public SelectProjectionBuilder select() {
        return new SelectProjectionBuilder(renderer);
    }

    public SelectBuilder select(String... columns) {
        return new SelectBuilder(renderer, columns);
    }

    public SelectBuilder selectAll() {
        return new SelectBuilder(renderer, "*");
    }

    public InsertBuilder insertInto(String tableName) {
        return new InsertBuilder(renderer, tableName);
    }

    public DeleteBuilder deleteFrom(String tableName) {
        return new DeleteBuilder(renderer, tableName);
    }

    public UpdateBuilder update(String tableName) {
        return new UpdateBuilder(renderer, tableName);
    }

    public MergeBuilder mergeInto(String targetTableName) {
        return new MergeBuilder(renderer, targetTableName);
    }
}
