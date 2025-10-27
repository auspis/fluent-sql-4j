package lan.tlab.r4j.sql.plugin.builtin.postgresql;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.fetch.FetchRenderStrategy;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape.EscapeStrategy;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;

/**
 * Built-in plugin for the PostgreSQL dialect.
 * <p>
 * This plugin provides support for rendering SQL statements according to PostgreSQL syntax
 * and semantics. It leverages PostgreSQL-specific rendering strategies for features such as
 * identifier escaping, pagination, and SQL functions.
 * <p>
 * <b>What is PostgreSQL?</b>
 * <p>
 * PostgreSQL is a powerful, open-source object-relational database management system (ORDBMS)
 * with a strong reputation for reliability, feature robustness, and performance. This plugin
 * implements PostgreSQL-specific SQL syntax and features, including:
 * <ul>
 *   <li>Double-quote identifier escaping ("identifier") per SQL standard</li>
 *   <li>LIMIT/OFFSET syntax for pagination</li>
 *   <li>Standard SQL || operator or CONCAT() function for string concatenation</li>
 *   <li>Standard SQL date and timestamp functions (CURRENT_DATE, CURRENT_TIMESTAMP)</li>
 *   <li>CHAR_LENGTH() or LENGTH() functions for string length operations</li>
 *   <li>Rich support for advanced SQL features (CTEs, window functions, JSON, arrays)</li>
 * </ul>
 * <p>
 * <b>PostgreSQL Version Compatibility:</b>
 * <p>
 * This plugin is designed to work with PostgreSQL 12.x and later versions. It uses the semantic
 * version range ">=12.0.0 <17.0.0" which matches all PostgreSQL 12.x, 13.x, 14.x, 15.x, and 16.x versions
 * (>=12.0.0 and &lt;17.0.0). PostgreSQL 12 introduced significant improvements and is a widely
 * adopted Long-Term Support (LTS) version.
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
 * SqlDialectPluginRegistry registry = SqlDialectPluginRegistry.createWithServiceLoader();
 * Result<DialectRenderer> result = registry.getRenderer("postgresql", "14.5");
 *
 * // Or created directly
 * SqlDialectPlugin plugin = PostgreSQLDialectPlugin.instance();
 * DialectRenderer renderer = plugin.createRenderer();
 * }</pre>
 * <p>
 * <b>Version Matching:</b>
 * <p>
 * This plugin uses semantic versioning with the range ">=12.0.0 <17.0.0". This means:
 * <ul>
 *   <li>Matches PostgreSQL 12.0, 12.1, 13.0, 14.5, 15.2, 16.0</li>
 *   <li>Does NOT match PostgreSQL 17.0 or later (major version change)</li>
 *   <li>Does NOT match PostgreSQL 11.x or earlier</li>
 * </ul>
 * <p>
 * <b>PostgreSQL-Specific Features Implemented:</b>
 * <ul>
 *   <li><b>Identifier Escaping:</b> Uses double quotes ("table"."column") per SQL standard</li>
 *   <li><b>Pagination:</b> Uses LIMIT n OFFSET m syntax</li>
 *   <li><b>Current Date:</b> Uses CURRENT_DATE per SQL standard</li>
 *   <li><b>Current Timestamp:</b> Uses CURRENT_TIMESTAMP or NOW() per SQL standard</li>
 *   <li><b>Date Arithmetic:</b> Uses standard SQL INTERVAL expressions</li>
 *   <li><b>String Concatenation:</b> Uses || operator per SQL standard</li>
 *   <li><b>String Length:</b> Uses CHAR_LENGTH() per SQL standard</li>
 * </ul>
 * <p>
 * <b>Advanced PostgreSQL Features:</b>
 * <ul>
 *   <li>CTEs (WITH clause): Fully supported in PostgreSQL 12+</li>
 *   <li>Window functions: Fully supported in PostgreSQL 12+</li>
 *   <li>LATERAL joins: Supported</li>
 *   <li>Array operations: Standard SQL array support</li>
 *   <li>JSON/JSONB: Rich JSON support available</li>
 *   <li>Full-text search: PostgreSQL-specific extensions available</li>
 *   <li>INSERT...ON CONFLICT (UPSERT): PostgreSQL-specific alternative to MERGE</li>
 * </ul>
 * <p>
 * <b>Limitations and Notes:</b>
 * <ul>
 *   <li>Standard SQL MERGE: Not natively supported, use INSERT...ON CONFLICT DO UPDATE instead</li>
 *   <li>For PostgreSQL 11 and earlier, some features may require different rendering strategies</li>
 *   <li>This plugin focuses on standard SQL features; PostgreSQL-specific extensions
 *       (e.g., arrays, JSON operators) may require custom strategies</li>
 * </ul>
 * <p>
 * <b>ServiceLoader Discovery:</b>
 * <p>
 * This plugin is automatically discovered through Java's {@link java.util.ServiceLoader}
 * mechanism via {@link PostgreSQLDialectPluginProvider}. The provider is registered in
 * {@code META-INF/services/lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider}.
 *
 * @see SqlDialectPlugin
 * @see PostgreSQLDialectPluginProvider
 * @see <a href="https://www.postgresql.org/docs/">PostgreSQL Documentation</a>
 * @see <a href="https://www.postgresql.org/docs/current/">PostgreSQL Current Documentation</a>
 * @since 1.0
 */
public final class PostgreSQLDialectPlugin {

    /**
     * The canonical name for the PostgreSQL dialect.
     * <p>
     * This name is used for plugin registration and lookup in the {@link lan.tlab.r4j.sql.plugin.SqlDialectPluginRegistry}.
     * The registry performs case-insensitive matching, so "PostgreSQL", "postgresql", and "POSTGRESQL"
     * will all match this dialect.
     */
    public static final String DIALECT_NAME = "PostgreSQL";

    /**
     * The version range of PostgreSQL supported by this plugin.
     * <p>
     * This uses semantic versioning with a range ">=12.0.0 <17.0.0", which means it supports
     * all PostgreSQL 12.x, 13.x, 14.x, 15.x, and 16.x versions (>=12.0.0 and &lt;17.0.0).
     * The registry will perform semantic version matching when resolving this plugin.
     * <p>
     * For PostgreSQL 11 or earlier, additional plugins can be registered with different
     * version ranges.
     */
    public static final String DIALECT_VERSION = ">=12.0.0 <17.0.0";

    private static final SqlDialectPlugin INSTANCE =
            new SqlDialectPlugin(DIALECT_NAME, DIALECT_VERSION, PostgreSQLDialectPlugin::createPostgreSqlRenderer);

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class follows the singleton pattern. Use {@link #instance()} to obtain
     * the plugin instance.
     */
    private PostgreSQLDialectPlugin() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates the PostgreSQL-specific renderers.
     * <p>
     * This method creates both the {@link SqlRenderer} and {@link PreparedStatementRenderer}
     * configured specifically for PostgreSQL syntax, including:
     * <ul>
     *   <li>Double-quote identifier escaping (SQL standard)</li>
     *   <li>LIMIT/OFFSET pagination syntax</li>
     *   <li>CURRENT_DATE for current date</li>
     *   <li>CURRENT_TIMESTAMP or NOW() for current timestamp</li>
     *   <li>Standard SQL date arithmetic with INTERVAL</li>
     *   <li>|| operator for string concatenation</li>
     *   <li>CHAR_LENGTH() for string length</li>
     * </ul>
     * <p>
     * PostgreSQL is highly compliant with the SQL standard, so most rendering strategies
     * use the standard SQL:2008 implementations. The primary difference is the use of
     * LIMIT/OFFSET syntax for pagination (similar to MySQL) instead of the verbose
     * OFFSET...ROWS FETCH NEXT...ROWS ONLY syntax.
     *
     * @return a new {@link DialectRenderer} instance configured for PostgreSQL, never {@code null}
     */
    private static DialectRenderer createPostgreSqlRenderer() {
        SqlRenderer sqlRenderer = SqlRenderer.builder()
                .escapeStrategy(EscapeStrategy.standard())
                .paginationStrategy(FetchRenderStrategy.mysql())
                .build();

        PreparedStatementRenderer psRenderer =
                PreparedStatementRenderer.builder().sqlRenderer(sqlRenderer).build();

        return new DialectRenderer(sqlRenderer, psRenderer);
    }

    /**
     * Returns the singleton instance of the PostgreSQL dialect plugin.
     * <p>
     * This method is thread-safe and always returns the same instance. The plugin
     * is immutable and can be safely shared across threads.
     * <p>
     * <b>Example usage:</b>
     * <pre>{@code
     * SqlDialectPlugin plugin = PostgreSQLDialectPlugin.instance();
     * DialectRenderer renderer = plugin.createRenderer();
     * }</pre>
     *
     * @return the singleton PostgreSQL dialect plugin instance, never {@code null}
     */
    public static SqlDialectPlugin instance() {
        return INSTANCE;
    }
}
