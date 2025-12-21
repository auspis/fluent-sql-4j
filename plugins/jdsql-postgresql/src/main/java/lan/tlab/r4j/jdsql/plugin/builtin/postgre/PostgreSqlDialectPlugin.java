package lan.tlab.r4j.jdsql.plugin.builtin.postgre;

import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.jdsql.plugin.builtin.postgre.dsl.PostgreSqlDSL;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlEscapeStrategy;

/**
 * Built-in plugin for the PostgreSQL dialect.
 * <p>
 * This plugin provides support for rendering SQL statements according to PostgreSQL syntax
 * and semantics. It leverages PostgreSQL-specific rendering strategies for features such as
 * string aggregation, array functions, JSON operations, and date/time manipulation.
 * <p>
 * <b>What is PostgreSQL?</b>
 * <p>
 * PostgreSQL is a powerful, open-source object-relational database system with a strong
 * reputation for reliability, feature robustness, and performance. This plugin implements
 * PostgreSQL-specific SQL syntax and features, including:
 * <ul>
 *   <li>STRING_AGG() function for string aggregation with ordering</li>
 *   <li>ARRAY_AGG() for array aggregation</li>
 *   <li>JSONB_AGG() for JSON aggregation</li>
 *   <li>TO_CHAR() for date/time formatting</li>
 *   <li>DATE_TRUNC() for date truncation</li>
 *   <li>AGE() for interval calculation</li>
 * </ul>
 * <p>
 * <b>PostgreSQL Version Compatibility:</b>
 * <p>
 * This plugin is designed to work with PostgreSQL 15.x and later versions. It uses the semantic
 * version range "^15.0.0" which matches all PostgreSQL 15.x versions (>=15.0.0 and <16.0.0).
 * Many features are also compatible with PostgreSQL 12+, but the plugin is optimized for
 * PostgreSQL 15 and newer.
 * <p>
 * <b>Design Characteristics:</b>
 * <ul>
 *   <li><b>Immutable</b>: This class is a singleton with no mutable state</li>
 *   <li><b>Thread-safe</b>: Can be safely used from multiple threads</li>
 *   <li><b>Stateless</b>: All rendering logic is delegated to the SqlRenderer</li>
 * </ul>
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * // Automatically discovered via ServiceLoader
 * SqlDialectRegistry registry = SqlDialectRegistry.createWithServiceLoader();
 * Result<PreparedStatementSpecFactory> result = registry.getSpecFactory("postgresql", "15.0.0");
 *
 * // Or created directly
 * SqlDialectPlugin plugin = PostgreSqlDialectPlugin.instance();
 * PreparedStatementSpecFactory specFactory = plugin.createRenderer();
 * }</pre>
 * <p>
 * <b>ServiceLoader Discovery:</b>
 * <p>
 * This plugin is automatically discovered through Java's {@link java.util.ServiceLoader}
 * mechanism via {@link PostgreSqlDialectPluginProvider}. The provider is registered in
 * {@code META-INF/services/lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider}.
 *
 * @see SqlDialectPlugin
 * @see PostgreSqlDialectPluginProvider
 * @since 1.0
 */
public final class PostgreSqlDialectPlugin {

    /**
     * The canonical name for the PostgreSQL dialect.
     * <p>
     * This name is used for plugin registration and lookup in the {@link lan.tlab.r4j.jdsql.plugin.SqlDialectPluginRegistry}.
     * The registry performs case-insensitive matching, so "PostgreSQL", "postgresql", and "POSTGRESQL"
     * will all match this dialect.
     */
    public static final String DIALECT_NAME = "PostgreSQL";

    /**
     * The version range of PostgreSQL supported by this plugin.
     * <p>
     * This uses semantic versioning with a caret range "^15.0.0", which means it supports
     * all PostgreSQL 15.x versions (>=15.0.0 and <16.0.0). The registry will perform semantic
     * version matching when resolving this plugin.
     */
    public static final String DIALECT_VERSION = "^15.0.0";

    private static final SqlDialectPlugin INSTANCE =
            new SqlDialectPlugin(DIALECT_NAME, DIALECT_VERSION, PostgreSqlDialectPlugin::dsl);

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class follows the singleton pattern. Use {@link #instance()} to obtain
     * the plugin instance.
     */
    private PostgreSqlDialectPlugin() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates the PostgreSQL-specific renderers.
     * <p>
     * This method creates both the {@link SqlRenderer} and {@link AstToPreparedStatementSpecVisitor}
     * configured specifically for PostgreSQL syntax, including:
     * <ul>
     *   <li>STRING_AGG for string aggregation</li>
     *   <li>ARRAY_AGG for array aggregation</li>
     *   <li>JSONB_AGG for JSON aggregation</li>
     *   <li>TO_CHAR for date formatting</li>
     *   <li>DATE_TRUNC for date truncation</li>
     *   <li>AGE for interval calculation</li>
     * </ul>
     *
     * @return a new {@link PreparedStatementSpecFactory} instance configured for PostgreSQL, never {@code null}
     */
    private static PreparedStatementSpecFactory createPreparedStatementSpecFactory() {
        AstToPreparedStatementSpecVisitor astToPsSpecVisitor = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new StandardSqlEscapeStrategy())
                .build();

        return new PreparedStatementSpecFactory(astToPsSpecVisitor);
    }

    /**
     * Creates a PostgreSQL-specific DSL instance.
     * <p>
     * Returns a {@link PostgreSqlDSL} instance configured with
     * the PostgreSQL renderer. This DSL provides PostgreSQL-specific custom functions like
     * {@code STRING_AGG}, {@code ARRAY_AGG}, {@code JSONB_AGG}, {@code TO_CHAR}, etc.
     * <p>
     * <b>Example usage:</b>
     * <pre>{@code
     * PostgreSqlDSL dsl = (PostgreSqlDSL) PostgreSqlDialectPlugin.instance().createDSL();
     * String sql = dsl.select(
     *     dsl.stringAgg("name")
     *         .separator(", ")
     *         .orderBy("name")
     *         .build()
     *         .as("names")
     * ).from("users").build();
     * }</pre>
     *
     * @return a new {@link PostgreSqlDSL} instance configured for PostgreSQL, never {@code null}
     */
    private static DSL dsl() {
        return new PostgreSqlDSL(createPreparedStatementSpecFactory());
    }

    /**
     * Returns the singleton instance of the PostgreSQL dialect plugin.
     * <p>
     * This method is thread-safe and always returns the same instance. The plugin
     * is immutable and can be safely shared across threads.
     * <p>
     * <b>Example usage:</b>
     * <pre>{@code
     * SqlDialectPlugin plugin = PostgreSqlDialectPlugin.instance();
     * PreparedStatementSpecFactory specFactory = plugin.createRenderer();
     * }</pre>
     *
     * @return the singleton PostgreSQL dialect plugin instance, never {@code null}
     */
    public static SqlDialectPlugin instance() {
        return INSTANCE;
    }
}
