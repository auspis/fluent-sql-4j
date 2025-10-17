package lan.tlab.r4j.sql.plugin;

import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

/**
 * Interface for SQL dialect plugins.
 * <p>
 * Implementations provide SQL-dialect-specific rendering capabilities through the plugin architecture.
 * Each plugin represents a specific SQL dialect (e.g., MySQL, PostgreSQL, SQL Server) and provides
 * a configured {@link SqlRenderer} that generates SQL statements conforming to that dialect's syntax.
 * <p>
 * Plugins are discovered and registered automatically via Java's {@link java.util.ServiceLoader}
 * mechanism. To create a plugin, implement this interface and register it in
 * {@code META-INF/services/lan.tlab.r4j.sql.plugin.SqlDialectPlugin}.
 * <p>
 * <b>Version Support:</b>
 * <p>
 * Plugins declare version support using Semantic Versioning (SemVer) notation through
 * {@link #getDialectVersion()}. The registry uses this information to match plugins to
 * requested database versions. Multiple plugins can be registered for the same dialect
 * with different version ranges.
 * <p>
 * <b>Example implementation:</b>
 * <pre>{@code
 * public class MySQL8DialectPlugin implements SqlDialectPlugin {
 *     @Override
 *     public String getDialectName() {
 *         return "mysql";
 *     }
 *
 *     @Override
 *     public String getDialectVersion() {
 *         return "^8.0.0";  // Supports all MySQL 8.x versions
 *     }
 *
 *     @Override
 *     public SqlRenderer createRenderer() {
 *         return SqlRendererFactory.mysql8();
 *     }
 * }
 * }</pre>
 *
 * @see SqlRenderer
 * @see SqlDialectRegistry
 * @since 1.0
 */
public interface SqlDialectPlugin {

    /**
     * Returns the canonical name of this SQL dialect.
     * <p>
     * The dialect name should be in lowercase and uniquely identify the dialect.
     * This is the primary identifier used for plugin registration and lookup.
     * Each plugin must have a unique dialect name.
     * <p>
     * <b>Examples:</b>
     * <ul>
     *   <li>{@code "mysql"} - MySQL database</li>
     *   <li>{@code "postgresql"} - PostgreSQL database</li>
     *   <li>{@code "mariadb"} - MariaDB database</li>
     *   <li>{@code "sqlserver"} - Microsoft SQL Server</li>
     *   <li>{@code "sql2008"} - Standard SQL:2008</li>
     * </ul>
     *
     * @return the canonical dialect name in lowercase, never {@code null}
     */
    String getDialectName();

    /**
     * Returns the version range of the SQL dialect that this plugin supports.
     * <p>
     * The version string must follow Semantic Versioning (SemVer) notation and can express:
     * <ul>
     *   <li>Exact version: {@code "8.0.35"}</li>
     *   <li>Caret range (compatible): {@code "^8.0.0"} (matches {@code >=8.0.0 <9.0.0})</li>
     *   <li>Tilde range (patch): {@code "~5.7.42"} (matches {@code >=5.7.42 <5.8.0})</li>
     *   <li>Explicit range: {@code ">=8.0.0 <9.0.0"}</li>
     *   <li>Compound conditions: {@code ">=5.7.0 <8.0.0 || >=8.0.20"}</li>
     * </ul>
     * <p>
     * The registry uses this information to select the appropriate plugin when a specific
     * database version is requested. If multiple plugins match the requested version,
     * the registry will use the first match and log a warning.
     * <p>
     * <b>Examples:</b>
     * <ul>
     *   <li>{@code "^8.0.0"} - MySQL 8.x (all 8.x versions)</li>
     *   <li>{@code ">=5.7.0 <8.0.0"} - MySQL 5.7.x series</li>
     *   <li>{@code "14.2"} - PostgreSQL 14.2 exactly</li>
     *   <li>{@code "^14.0.0"} - PostgreSQL 14.x series</li>
     * </ul>
     *
     * @return the SemVer version range string, never {@code null}
     * @see <a href="https://semver.org/">Semantic Versioning</a>
     * @see <a href="https://github.com/npm/node-semver">NPM semver ranges</a>
     */
    String getDialectVersion();

    /**
     * Creates a new {@link SqlRenderer} configured for this SQL dialect.
     * <p>
     * The renderer is responsible for converting the abstract syntax tree (AST) representation
     * of SQL statements into dialect-specific SQL text. Each invocation should return a new
     * instance to ensure thread safety.
     * <p>
     * The returned renderer should be configured with all the necessary strategies and escape
     * rules appropriate for the dialect and version that this plugin supports.
     *
     * @return a new, fully configured {@link SqlRenderer} instance, never {@code null}
     */
    SqlRenderer createRenderer();
}
