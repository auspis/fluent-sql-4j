package lan.tlab.r4j.sql.plugin;

import java.util.Set;
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
 * {@code META-INF/services/lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin}.
 * <p>
 * <b>Example implementation:</b>
 * <pre>{@code
 * public class MySQLDialectPlugin implements SqlDialectPlugin {
 *     @Override
 *     public String getDialectName() {
 *         return "mysql";
 *     }
 *
 *     @Override
 *     public String getVersion() {
 *         return "8.0";
 *     }
 *
 *     @Override
 *     public SqlRenderer createRenderer() {
 *         return SqlRendererFactory.mysql();
 *     }
 *
 *     @Override
 *     public boolean supports(String dialectName) {
 *         return "mysql".equalsIgnoreCase(dialectName)
 *             || "mariadb".equalsIgnoreCase(dialectName);
 *     }
 *
 *     @Override
 *     public Set<String> getSupportedFeatures() {
 *         return Set.of("limit", "offset", "cte", "window_functions");
 *     }
 * }
 * }</pre>
 *
 * @see SqlRenderer
 * @since 1.0
 */
public interface SqlDialectPlugin {

    /**
     * Returns the canonical name of this SQL dialect.
     * <p>
     * The dialect name should be in lowercase and uniquely identify the dialect.
     * This is the primary identifier used for plugin registration and lookup.
     * <p>
     * <b>Examples:</b>
     * <ul>
     *   <li>{@code "mysql"} - MySQL database</li>
     *   <li>{@code "postgresql"} - PostgreSQL database</li>
     *   <li>{@code "sqlserver"} - Microsoft SQL Server</li>
     *   <li>{@code "sql2008"} - Standard SQL:2008</li>
     * </ul>
     *
     * @return the canonical dialect name in lowercase, never {@code null}
     */
    String getDialectName();

    /**
     * Returns the version of the SQL dialect that this plugin supports.
     * <p>
     * The version string should follow semantic versioning principles where applicable.
     * This allows applications to select plugins based on specific database version requirements.
     * <p>
     * <b>Examples:</b>
     * <ul>
     *   <li>{@code "8.0"} - MySQL 8.0</li>
     *   <li>{@code "14.0"} - PostgreSQL 14</li>
     *   <li>{@code "2019"} - SQL Server 2019</li>
     *   <li>{@code "2008"} - Standard SQL:2008</li>
     * </ul>
     *
     * @return the version string, never {@code null}
     */
    String getVersion();

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

    /**
     * Checks if this plugin supports the given dialect name.
     * <p>
     * This method allows plugins to support multiple dialect names, including aliases.
     * For example, a MySQL plugin might support "mysql" and "mariadb", while a PostgreSQL
     * plugin might support "postgresql", "postgres", and "pg".
     * <p>
     * The comparison should be case-insensitive.
     * <p>
     * <b>Examples:</b>
     * <ul>
     *   <li>MySQL plugin: supports "mysql" and "mariadb"</li>
     *   <li>PostgreSQL plugin: supports "postgresql", "postgres", and "pg"</li>
     *   <li>SQL Server plugin: supports "sqlserver", "mssql", and "tsql"</li>
     *   <li>Standard SQL plugin: supports "sql2008", "standard", and "ansi"</li>
     * </ul>
     *
     * @param dialectName the dialect name to check, may be {@code null}
     * @return {@code true} if this plugin supports the given dialect name, {@code false} otherwise
     */
    boolean supports(String dialectName);

    /**
     * Returns the set of feature identifiers that this dialect supports.
     * <p>
     * Features represent specific SQL capabilities that may vary across dialects, such as:
     * <ul>
     *   <li>{@code "cte"} - Common Table Expressions (WITH clause)</li>
     *   <li>{@code "window_functions"} - Window functions (OVER clause)</li>
     *   <li>{@code "recursive_cte"} - Recursive CTEs</li>
     *   <li>{@code "limit"} - LIMIT clause for pagination</li>
     *   <li>{@code "offset"} - OFFSET clause for pagination</li>
     *   <li>{@code "returning"} - RETURNING clause in DML statements</li>
     *   <li>{@code "lateral_join"} - LATERAL joins</li>
     *   <li>{@code "json"} - JSON data type and functions</li>
     * </ul>
     * <p>
     * The returned set should be immutable or defensively copied to prevent modification.
     * Feature identifiers should be in lowercase for consistency.
     *
     * @return an immutable set of supported feature identifiers, never {@code null}
     */
    Set<String> getSupportedFeatures();
}
