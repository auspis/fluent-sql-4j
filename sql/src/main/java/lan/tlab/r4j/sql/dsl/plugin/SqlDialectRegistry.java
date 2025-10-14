package lan.tlab.r4j.sql.dsl.plugin;

import java.util.Collections;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

/**
 * Central registry for SQL dialect plugins.
 * <p>
 * This registry manages all SQL dialect plugins and provides thread-safe access to them.
 * Plugins are automatically discovered using Java's {@link ServiceLoader} mechanism (SPI)
 * at class initialization time. Additional plugins can be registered dynamically using
 * the {@link #register(SqlDialectPlugin)} method.
 * <p>
 * The registry provides a clean API for:
 * <ul>
 *   <li>Retrieving renderers for specific dialects</li>
 *   <li>Checking if a dialect is supported</li>
 *   <li>Listing all supported dialects</li>
 * </ul>
 * <p>
 * Dialect names are handled case-insensitively - they are normalized to lowercase
 * when stored and retrieved. This means "MySQL", "mysql", and "MYSQL" all refer
 * to the same dialect.
 * <p>
 * <b>Thread Safety:</b> This class is thread-safe. The internal storage uses
 * {@link ConcurrentHashMap} to ensure safe concurrent access.
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * // Check if a dialect is supported
 * if (SqlDialectRegistry.isSupported("mysql")) {
 *     SqlRenderer renderer = SqlDialectRegistry.getRenderer("mysql");
 *     // Use the renderer...
 * }
 *
 * // Get all supported dialects
 * Set<String> dialects = SqlDialectRegistry.getSupportedDialects();
 * System.out.println("Supported: " + dialects);
 *
 * // Register a custom plugin
 * SqlDialectPlugin customPlugin = new MyCustomDialectPlugin();
 * SqlDialectRegistry.register(customPlugin);
 * }</pre>
 *
 * @see SqlDialectPlugin
 * @see SqlRenderer
 * @since 1.0
 */
public final class SqlDialectRegistry {

    /**
     * Thread-safe storage for registered plugins.
     * Keys are dialect names in lowercase, values are the plugin instances.
     */
    private static final ConcurrentHashMap<String, SqlDialectPlugin> plugins = new ConcurrentHashMap<>();

    static {
        // Auto-discovery of plugins via ServiceLoader
        ServiceLoader<SqlDialectPlugin> loader = ServiceLoader.load(SqlDialectPlugin.class);
        loader.forEach(SqlDialectRegistry::register);
    }

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private SqlDialectRegistry() {
        // Utility class - prevent instantiation
    }

    /**
     * Registers a SQL dialect plugin.
     * <p>
     * The plugin is registered under its canonical dialect name (obtained via
     * {@link SqlDialectPlugin#getDialectName()}). If a plugin is already registered
     * for the same dialect, it will be replaced with the new plugin.
     * <p>
     * This method is thread-safe and can be called concurrently.
     *
     * @param plugin the plugin to register, must not be {@code null}
     * @throws NullPointerException if {@code plugin} is {@code null}
     * @throws NullPointerException if {@code plugin.getDialectName()} returns {@code null}
     */
    public static void register(SqlDialectPlugin plugin) {
        if (plugin == null) {
            throw new NullPointerException("Plugin must not be null");
        }
        String dialectName = plugin.getDialectName();
        if (dialectName == null) {
            throw new NullPointerException("Plugin dialect name must not be null");
        }
        plugins.put(dialectName.toLowerCase(), plugin);
    }

    /**
     * Retrieves a {@link SqlRenderer} for the specified SQL dialect.
     * <p>
     * The dialect name is matched case-insensitively. The registry first looks for
     * a plugin registered under the exact dialect name, then checks all plugins
     * to see if any support the requested dialect (via {@link SqlDialectPlugin#supports(String)}).
     * <p>
     * Each call returns a new renderer instance to ensure thread safety.
     *
     * @param dialect the name of the SQL dialect, must not be {@code null}
     * @return a new {@link SqlRenderer} instance configured for the dialect
     * @throws IllegalArgumentException if the dialect is not supported or if {@code dialect} is {@code null}
     */
    public static SqlRenderer getRenderer(String dialect) {
        if (dialect == null) {
            throw new IllegalArgumentException("Dialect name must not be null");
        }

        String normalizedDialect = dialect.toLowerCase();

        // First, try direct lookup by dialect name
        SqlDialectPlugin plugin = plugins.get(normalizedDialect);

        // If not found, check if any plugin supports this dialect (for aliases)
        if (plugin == null) {
            plugin = plugins.values().stream()
                    .filter(p -> p.supports(dialect))
                    .findFirst()
                    .orElse(null);
        }

        if (plugin == null) {
            throw new IllegalArgumentException(
                    "Unsupported SQL dialect: '" + dialect + "'. Supported dialects: " + getSupportedDialects());
        }

        return plugin.createRenderer();
    }

    /**
     * Returns an immutable set of all supported SQL dialect names.
     * <p>
     * The returned set contains the canonical names of all registered dialects
     * in lowercase. The set is a snapshot and will not reflect future registrations.
     * <p>
     * Note: This method returns only the canonical names. Dialects may support
     * additional aliases that are not included in this set. Use {@link #isSupported(String)}
     * to check if a specific dialect name (including aliases) is supported.
     *
     * @return an immutable set of supported dialect names, never {@code null}
     */
    public static Set<String> getSupportedDialects() {
        return Collections.unmodifiableSet(plugins.keySet());
    }

    /**
     * Checks if the specified SQL dialect is supported.
     * <p>
     * This method checks both canonical dialect names and aliases. The check is
     * case-insensitive.
     *
     * @param dialect the name of the SQL dialect to check, may be {@code null}
     * @return {@code true} if the dialect is supported, {@code false} otherwise
     */
    public static boolean isSupported(String dialect) {
        if (dialect == null) {
            return false;
        }

        String normalizedDialect = dialect.toLowerCase();

        // Check direct lookup first
        if (plugins.containsKey(normalizedDialect)) {
            return true;
        }

        // Check if any plugin supports this dialect (for aliases)
        return plugins.values().stream().anyMatch(p -> p.supports(dialect));
    }
}
