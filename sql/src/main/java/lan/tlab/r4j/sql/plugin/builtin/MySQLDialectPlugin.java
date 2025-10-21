package lan.tlab.r4j.sql.plugin.builtin;

import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;

/**
 * Built-in plugin for the MySQL dialect.
 * <p>
 * This plugin provides support for rendering SQL statements according to MySQL's SQL dialect.
 * It leverages MySQL-specific rendering strategies for identifier escaping, pagination,
 * date/time functions, string concatenation, and other MySQL-specific features.
 * <p>
 * <b>What is MySQL?</b>
 * <p>
 * MySQL is one of the most popular open-source relational database management systems.
 * This plugin supports MySQL 8.0 and later versions, which include modern SQL features
 * such as window functions, CTEs, and the EXCEPT set operation.
 * <p>
 * <b>MySQL-Specific Features Supported:</b>
 * <ul>
 *   <li><b>Backtick identifier escaping</b>: Uses backticks (`) instead of double quotes (")</li>
 *   <li><b>LIMIT/OFFSET pagination</b>: Uses {@code LIMIT n OFFSET m} syntax</li>
 *   <li><b>CONCAT() function</b>: String concatenation using {@code CONCAT()} instead of {@code ||}</li>
 *   <li><b>DATE_ADD/DATE_SUB</b>: Date arithmetic using MySQL-specific functions</li>
 *   <li><b>NOW() and CURDATE()</b>: Current timestamp and date functions</li>
 *   <li><b>CHAR_LENGTH()</b>: String length function</li>
 * </ul>
 * <p>
 * <b>MariaDB Compatibility:</b>
 * <p>
 * MariaDB is a MySQL fork that maintains protocol and SQL syntax compatibility with MySQL.
 * This plugin works with MariaDB databases as well, though MariaDB-specific extensions
 * (like sequences) are not explicitly supported.
 * <p>
 * <b>Version Compatibility:</b>
 * <p>
 * This plugin is designed for MySQL 8.0 and later, using semantic versioning with
 * a caret (^) notation to indicate compatibility with all MySQL 8.x versions. While
 * it may work with MySQL 5.7, some features like the EXCEPT operator are only available
 * in MySQL 8.0.31+. The version string "^8.0.0" matches MySQL versions >=8.0.0 and <9.0.0.
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
 * RegistryResult<SqlRenderer> result = registry.getRenderer("mysql");
 *
 * // Or created directly
 * SqlDialectPlugin plugin = MySQLDialectPlugin.instance();
 * SqlRenderer renderer = plugin.createRenderer();
 * }</pre>
 * <p>
 * <b>Version Matching:</b>
 * <p>
 * This plugin uses semantic versioning with the caret (^) notation. The version "^8.0.0"
 * indicates compatibility with all MySQL 8.x versions (>=8.0.0 and <9.0.0). The registry
 * uses this information to match compatible MySQL versions when resolving the plugin.
 * <p>
 * <b>ServiceLoader Discovery:</b>
 * <p>
 * This plugin is automatically discovered through Java's {@link java.util.ServiceLoader}
 * mechanism via {@link MySQLDialectPluginProvider}. The provider is registered in
 * {@code META-INF/services/lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider}.
 * <p>
 * <b>Limitations:</b>
 * <ul>
 *   <li>EXCEPT operator requires MySQL 8.0.31 or later</li>
 *   <li>Window functions require MySQL 8.0 or later</li>
 *   <li>CTEs (WITH clause) require MySQL 8.0 or later</li>
 *   <li>MariaDB-specific features (sequences, RETURNING clause) are not supported</li>
 * </ul>
 *
 * @see SqlDialectPlugin
 * @see MySQLDialectPluginProvider
 * @see SqlRendererFactory#mysql()
 * @see <a href="https://dev.mysql.com/doc/">MySQL Documentation</a>
 * @see <a href="https://mariadb.org/documentation/">MariaDB Documentation</a>
 * @since 1.0
 */
public final class MySQLDialectPlugin {

    /**
     * The canonical name for the MySQL dialect.
     * <p>
     * This name is used for plugin registration and lookup in the {@link lan.tlab.r4j.sql.plugin.SqlDialectRegistry}.
     * The registry performs case-insensitive matching, so "MySQL", "mysql", and "MYSQL"
     * will all match this dialect.
     */
    public static final String DIALECT_NAME = "MySQL";

    /**
     * The version of MySQL supported by this plugin.
     * <p>
     * This version string uses semantic versioning to indicate MySQL 8.0 and later versions.
     * The caret (^) notation means this plugin is compatible with all MySQL 8.x versions
     * (>=8.0.0 and <9.0.0). This version range includes MySQL 8.0 LTS and all subsequent
     * 8.x releases while excluding breaking changes in MySQL 9.0+.
     */
    public static final String DIALECT_VERSION = "^8.0.0";

    private static final SqlDialectPlugin INSTANCE =
            new SqlDialectPlugin(DIALECT_NAME, DIALECT_VERSION, SqlRendererFactory::mysql);

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class follows the singleton pattern. Use {@link #instance()} to obtain
     * the plugin instance.
     */
    private MySQLDialectPlugin() {
        // Utility class - prevent instantiation
    }

    /**
     * Returns the singleton instance of the MySQL dialect plugin.
     * <p>
     * This method is thread-safe and always returns the same instance. The plugin
     * is immutable and can be safely shared across threads.
     * <p>
     * <b>Example usage:</b>
     * <pre>{@code
     * SqlDialectPlugin plugin = MySQLDialectPlugin.instance();
     * SqlRenderer renderer = plugin.createRenderer();
     * }</pre>
     *
     * @return the singleton MySQL dialect plugin instance, never {@code null}
     */
    public static SqlDialectPlugin instance() {
        return INSTANCE;
    }
}
