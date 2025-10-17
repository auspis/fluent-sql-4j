package lan.tlab.r4j.sql.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.util.VersionFormatException;
import lan.tlab.r4j.sql.plugin.util.VersionMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SqlDialectRegistry {

    private static final Logger logger = LoggerFactory.getLogger(SqlDialectRegistry.class);

    /**
     * Thread-safe storage for registered plugins.
     * Keys are dialect names in lowercase, values are lists of plugins for that dialect.
     * Multiple plugins can exist for the same dialect with different version ranges.
     */
    private static final ConcurrentHashMap<String, List<SqlDialectPlugin>> plugins = new ConcurrentHashMap<>();

    static {
        ServiceLoader<SqlDialectPlugin> loader = ServiceLoader.load(SqlDialectPlugin.class);
        loader.forEach(SqlDialectRegistry::register);
    }

    private SqlDialectRegistry() {}

    /**
     * Registers a SQL dialect plugin.
     * <p>
     * The plugin is registered under its canonical dialect name (obtained via
     * {@link SqlDialectPlugin#getDialectName()}). Multiple plugins can be registered
     * for the same dialect with different version ranges.
     * <p>
     * This method is thread-safe and can be called concurrently.
     *
     * @param plugin the plugin to register, must not be {@code null}
     * @throws NullPointerException if {@code plugin} is {@code null}
     * @throws NullPointerException if {@code plugin.getDialectName()} returns {@code null}
     * @throws NullPointerException if {@code plugin.getDialectVersion()} returns {@code null}
     */
    public static void register(SqlDialectPlugin plugin) {
        if (plugin == null) {
            throw new NullPointerException("Plugin must not be null");
        }
        String dialectName = plugin.getDialectName();
        if (dialectName == null) {
            throw new NullPointerException("Plugin dialect name must not be null");
        }
        String dialectVersion = plugin.getDialectVersion();
        if (dialectVersion == null) {
            throw new NullPointerException("Plugin dialect version must not be null");
        }

        String normalizedDialect = dialectName.toLowerCase();
        plugins.computeIfAbsent(normalizedDialect, k -> new ArrayList<>()).add(plugin);
    }

    /**
     * Retrieves a {@link SqlRenderer} for the specified SQL dialect without version matching.
     * <p>
     * This method returns the first available plugin for the given dialect, regardless of version.
     * If you need version-specific rendering, use {@link #getRenderer(String, String)} instead.
     * <p>
     * The dialect name is matched case-insensitively.
     * Each call returns a new renderer instance to ensure thread safety.
     *
     * @param dialect the name of the SQL dialect, must not be {@code null}
     * @return a new {@link SqlRenderer} instance configured for the dialect
     * @throws IllegalArgumentException if the dialect is not supported or if {@code dialect} is {@code null}
     */
    public static SqlRenderer getRenderer(String dialect) {
        return getRenderer(dialect, null);
    }

    /**
     * Retrieves a {@link SqlRenderer} for the specified SQL dialect and version.
     * <p>
     * The dialect name is matched case-insensitively. The registry finds all plugins
     * registered for the given dialect and filters them by version compatibility using
     * Semantic Versioning (SemVer) matching.
     * <p>
     * If multiple plugins match the requested version, the first match is used and a warning
     * is logged. This typically indicates overlapping version ranges in plugin configuration.
     * <p>
     * Each call returns a new renderer instance to ensure thread safety.
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * // Get renderer for MySQL 8.0.35
     * SqlRenderer renderer = SqlDialectRegistry.getRenderer("mysql", "8.0.35");
     * }</pre>
     *
     * @param dialect the name of the SQL dialect, must not be {@code null}
     * @param version the database version (SemVer format), may be {@code null} to match any version
     * @return a new {@link SqlRenderer} instance configured for the dialect and version
     * @throws IllegalArgumentException if the dialect is not supported, if no plugin matches the version,
     *                                  if the version format is invalid, or if {@code dialect} is {@code null}
     */
    public static SqlRenderer getRenderer(String dialect, String version) {
        if (dialect == null) {
            throw new IllegalArgumentException("Dialect name must not be null");
        }

        List<SqlDialectPlugin> matchingPlugins = findMatchingPlugins(dialect, version);

        if (matchingPlugins.isEmpty()) {
            String versionInfo = version != null ? " version '" + version + "'" : "";
            throw new IllegalArgumentException("No plugin found for dialect '" + dialect + "'" + versionInfo
                    + ". Supported dialects: " + getSupportedDialects());
        }

        if (matchingPlugins.size() > 1) {
            String pluginInfo = matchingPlugins.stream()
                    .map(p -> p.getDialectName() + ":" + p.getDialectVersion())
                    .collect(Collectors.joining(", "));
            logger.warn(
                    "Multiple plugins match dialect '{}' version '{}': [{}]. Using first match: {}",
                    dialect,
                    version,
                    pluginInfo,
                    matchingPlugins.get(0).getDialectName() + ":"
                            + matchingPlugins.get(0).getDialectVersion());
        }

        return matchingPlugins.get(0).createRenderer();
    }

    /**
     * Finds all plugins matching the specified dialect and version.
     *
     * @param dialect the dialect name (case-insensitive)
     * @param version the version to match (SemVer format), or null to match any version
     * @return list of matching plugins, empty if none found
     */
    private static List<SqlDialectPlugin> findMatchingPlugins(String dialect, String version) {
        String normalizedDialect = dialect.toLowerCase();
        List<SqlDialectPlugin> dialectPlugins = plugins.get(normalizedDialect);

        if (dialectPlugins == null || dialectPlugins.isEmpty()) {
            return Collections.emptyList();
        }

        // If no version specified, return all plugins for the dialect
        if (version == null || version.trim().isEmpty()) {
            return dialectPlugins;
        }

        // Validate version format
        if (!VersionMatcher.isValidVersion(version)) {
            throw new IllegalArgumentException("Invalid version format: '" + version + "'");
        }

        // Filter plugins by version compatibility
        return dialectPlugins.stream()
                .filter(plugin -> {
                    try {
                        return VersionMatcher.matches(version, plugin.getDialectVersion());
                    } catch (VersionFormatException e) {
                        logger.warn(
                                "Invalid version range '{}' in plugin {}, skipping",
                                plugin.getDialectVersion(),
                                plugin.getDialectName(),
                                e);
                        return false;
                    }
                })
                .collect(Collectors.toList());
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
     * The check is case-insensitive and only verifies if at least one plugin
     * is registered for the given dialect name.
     *
     * @param dialect the name of the SQL dialect to check, may be {@code null}
     * @return {@code true} if the dialect is supported, {@code false} otherwise
     */
    public static boolean isSupported(String dialect) {
        if (dialect == null) {
            return false;
        }

        String normalizedDialect = dialect.toLowerCase();
        return plugins.containsKey(normalizedDialect);
    }

    /**
     * Clears all registered plugins from the registry.
     * <p>
     * This method is primarily intended for testing purposes to ensure test isolation.
     * <b>Warning:</b> This will remove all registered plugins and should not be used
     * in production code.
     */
    static void clear() {
        plugins.clear();
    }
}
