package io.github.auspis.fluentsql4j.dsl;

import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.delete.DeleteBuilder;
import io.github.auspis.fluentsql4j.dsl.insert.InsertBuilder;
import io.github.auspis.fluentsql4j.dsl.merge.MergeBuilder;
import io.github.auspis.fluentsql4j.dsl.select.SelectBuilder;
import io.github.auspis.fluentsql4j.dsl.select.SelectProjectionBuilder;
import io.github.auspis.fluentsql4j.dsl.table.CreateTableBuilder;
import io.github.auspis.fluentsql4j.dsl.truncate.TruncateBuilder;
import io.github.auspis.fluentsql4j.dsl.update.UpdateBuilder;
import io.github.auspis.fluentsql4j.hook.build.BuildHookFactory;
import java.util.Objects;

/**
 * Base DSL class for building SQL queries in a type-safe, fluent manner.
 * <p>
 * This class serves as the foundation for dialect-specific DSL implementations.
 * Subclasses can extend this class to add dialect-specific functionality, such as
 * custom SQL functions (e.g., MySQL's GROUP_CONCAT, IF, DATE_FORMAT).
 * <p>
 */
public class DSL {

    protected final PreparedStatementSpecFactory specFactory;

    /**
     * Creates a DSL instance configured for a specific SQL dialect.
     * <p>
     * This constructor is used by {@link DSLRegistry} to create dialect-specific
     * DSL instances. It can also be used directly when you have a {@link PreparedStatementSpecFactory}
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
     * PreparedStatementSpecFactory specFactory = ...; // obtain from SqlDialectPluginRegistry
     * DSL dsl = new DSL(specFactory);
     * String sql = dsl.select("name").from("users").build();
     * }</pre>
     *
     * @param specFactory the dialect PreparedStatementSpecFactory to use for this DSL instance
     * @throws NullPointerException if {@code specFactory} is {@code null}
     * @see DSLRegistry
     */
    public DSL(PreparedStatementSpecFactory specFactory) {
        this.specFactory = Objects.requireNonNull(specFactory, "PreparedStatementSpecFactory must not be null");
    }

    /**
     * Returns the spec factory used by this DSL instance.
     * <p>
     * This method provides access to the underlying {@link PreparedStatementSpecFactory} for
     * advanced use cases where direct access to the factory is needed.
     *
     * @return the spec factory, never {@code null}
     */
    public PreparedStatementSpecFactory getSpecFactory() {
        return specFactory;
    }

    public CreateTableBuilder createTable(String tableName) {
        return new CreateTableBuilder(specFactory, tableName);
    }

    public TruncateBuilder truncateTable(String tableName) {
        return new TruncateBuilder(specFactory, tableName);
    }

    public <SELF extends SelectProjectionBuilder<SELF>> SelectProjectionBuilder<SELF> select() {
        return new SelectProjectionBuilder<>(specFactory);
    }

    public SelectBuilder select(String... columns) {
        return new SelectBuilder(specFactory, columns);
    }

    public SelectBuilder selectAll() {
        return new SelectBuilder(specFactory, "*");
    }

    public InsertBuilder insertInto(String tableName) {
        return new InsertBuilder(specFactory, tableName);
    }

    public DeleteBuilder deleteFrom(String tableName) {
        return new DeleteBuilder(specFactory, tableName);
    }

    public UpdateBuilder update(String tableName) {
        return new UpdateBuilder(specFactory, tableName);
    }

    public MergeBuilder mergeInto(String targetTableName) {
        return new MergeBuilder(specFactory, targetTableName);
    }

    /**
     * Returns a new DSL instance identical to this one but with the given hook factory.
     *
     * <p>The dialect visitor is preserved; only the hook factory is replaced. Subclasses
     * maintain their specific type via {@link #newInstance(PreparedStatementSpecFactory)}.
     *
     * @param hookFactory the hook factory to use; must not be {@code null}
     * @return a new DSL (or subclass) instance with the given hook factory
     */
    public DSL withBuildHookFactory(BuildHookFactory hookFactory) {
        Objects.requireNonNull(hookFactory, "BuildHookFactory must not be null");
        PreparedStatementSpecFactory newSpecFactory =
                new PreparedStatementSpecFactory(specFactory.astVisitor(), hookFactory);
        return newInstance(newSpecFactory);
    }

    /**
     * Template method called by {@link #withBuildHookFactory} to produce a DSL instance of
     * the correct subtype. Subclasses must override this to return their own type.
     *
     * @param newSpecFactory the spec factory for the new instance
     * @return a new DSL or subclass instance
     */
    protected DSL newInstance(PreparedStatementSpecFactory newSpecFactory) {
        return new DSL(newSpecFactory);
    }
}
