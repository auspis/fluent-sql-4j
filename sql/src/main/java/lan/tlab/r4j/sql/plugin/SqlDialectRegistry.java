package lan.tlab.r4j.sql.plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.RegistryResult.Failure;
import lan.tlab.r4j.sql.plugin.RegistryResult.Success;
import lan.tlab.r4j.sql.plugin.util.SemVerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Immutable registry for SQL dialect plugins.
 * <p>
 * This registry follows functional programming principles:
 * <ul>
 *   <li><b>Immutability</b>: Once created, a registry instance cannot be modified</li>
 *   <li><b>Pure functions</b>: Query methods have no side effects</li>
 *   <li><b>No exceptions</b>: Operations return {@link RegistryResult} instead of throwing</li>
 *   <li><b>Thread-safe by design</b>: Immutability eliminates concurrency issues</li>
 * </ul>
 * <p>
 * <b>Creating a registry:</b>
 * <pre>{@code
 * // Load plugins via ServiceLoader
 * SqlDialectRegistry registry = SqlDialectRegistry.createWithServiceLoader();
 *
 * // Create empty registry
 * SqlDialectRegistry registry = SqlDialectRegistry.empty();
 *
 * // Create with specific plugins
 * SqlDialectRegistry registry = SqlDialectRegistry.of(List.of(plugin1, plugin2));
 * }</pre>
 * <p>
 * <b>Using the registry:</b>
 * <pre>{@code
 * RegistryResult<SqlRenderer> result = registry.getRenderer("mysql", "8.0.35");
 *
 * switch (result) {
 *     case Success<SqlRenderer>(SqlRenderer renderer) -> // use renderer
 *     case Failure<SqlRenderer>(String message) -> // handle error
 * }
 *
 * // Or use helper methods
 * SqlRenderer renderer = registry.getRenderer("mysql", "8.0.35").orElseThrow();
 * }</pre>
 * <p>
 * <b>Registering plugins:</b>
 * <pre>{@code
 * // Registration returns a NEW registry instance
 * SqlDialectRegistry newRegistry = registry.register(customPlugin);
 * }</pre>
 *
 * @see SqlDialectPlugin
 * @see SqlDialectPluginProvider
 * @see RegistryResult
 * @since 1.0
 */
public final class SqlDialectRegistry {

    private static final Logger logger = LoggerFactory.getLogger(SqlDialectRegistry.class);

    /**
     * Immutable storage for registered plugins.
     * Keys are dialect names in lowercase, values are immutable lists of plugins for that dialect.
     */
    private final Map<String, List<SqlDialectPlugin>> plugins;

    /**
     * Private constructor. Use factory methods to create instances.
     *
     * @param plugins the plugins to include in this registry
     */
    private SqlDialectRegistry(Map<String, List<SqlDialectPlugin>> plugins) {
        Map<String, List<SqlDialectPlugin>> copy = new HashMap<>();
        plugins.forEach((key, value) -> copy.put(key, List.copyOf(value)));
        this.plugins = Map.copyOf(copy);
    }

    /**
     * Creates a registry with plugins discovered via {@link ServiceLoader}.
     * <p>
     * This is the recommended way to create a registry in production code.
     * It automatically discovers and loads all {@link SqlDialectPluginProvider}
     * implementations available on the classpath.
     * <p>
     * If no plugins are found, returns an empty registry.
     *
     * @return a new registry instance with all discovered plugins
     */
    public static SqlDialectRegistry createWithServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> loader = ServiceLoader.load(SqlDialectPluginProvider.class);

        List<SqlDialectPlugin> plugins = loader.stream()
                .map(ServiceLoader.Provider::get)
                .map(SqlDialectPluginProvider::get)
                .peek(plugin ->
                        logger.debug("Loaded plugin: {} version {}", plugin.dialectName(), plugin.dialectVersion()))
                .toList();

        long distinctDialects = plugins.stream()
                .map(p -> getNormalizedDialect(p.dialectName()))
                .distinct()
                .count();
        logger.info("Loaded {} plugin(s) for {} dialect(s)", plugins.size(), distinctDialects);

        return of(plugins);
    }

    /**
     * Creates an empty registry.
     * <p>
     * This is useful for testing or when you want to build a registry manually
     * using {@link #register(SqlDialectPlugin)}.
     *
     * @return a new empty registry instance
     */
    public static SqlDialectRegistry empty() {
        return new SqlDialectRegistry(Map.of());
    }

    /**
     * Creates a registry with the specified plugins.
     * <p>
     * This is useful for testing when you want to create a registry with
     * a specific set of plugins.
     *
     * @param pluginList the plugins to include in the registry
     * @return a new registry instance containing the specified plugins
     * @throws NullPointerException if {@code pluginList} is {@code null} or contains {@code null} elements
     */
    public static SqlDialectRegistry of(List<SqlDialectPlugin> pluginList) {
        Objects.requireNonNull(pluginList, "Plugins list must not be null");

        Map<String, List<SqlDialectPlugin>> pluginMap = pluginList.stream()
                .peek(plugin -> Objects.requireNonNull(plugin, "Plugin list must not contain null elements"))
                .collect(Collectors.groupingBy(
                        plugin -> getNormalizedDialect(plugin.dialectName()), LinkedHashMap::new, Collectors.toList()));

        return new SqlDialectRegistry(pluginMap);
    }

    /**
     * Registers a SQL dialect plugin, returning a new registry instance.
     * <p>
     * This method does not modify the current registry. Instead, it returns
     * a new registry instance that includes the specified plugin in addition
     * to all plugins from this registry.
     * <p>
     * This immutable approach ensures thread safety and makes the registry
     * behavior predictable and testable.
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * SqlDialectRegistry registry = SqlDialectRegistry.empty();
     * SqlDialectRegistry newRegistry = registry.register(plugin1).register(plugin2);
     * // registry is still empty, newRegistry contains both plugins
     * }</pre>
     *
     * @param plugin the plugin to register, must not be {@code null}
     * @return a new registry instance containing this plugin and all existing plugins
     * @throws NullPointerException if {@code plugin} is {@code null}
     */
    public SqlDialectRegistry register(SqlDialectPlugin plugin) {
        Objects.requireNonNull(plugin, "Plugin must not be null");

        List<SqlDialectPlugin> allPlugins = Stream.concat(
                        this.plugins.values().stream().flatMap(List::stream), Stream.of(plugin))
                .toList();

        return of(allPlugins);
    }

    /**
     * Retrieves a {@link SqlRenderer} for the specified SQL dialect without version matching.
     * <p>
     * This method returns the first available plugin for the given dialect, regardless of version.
     * If you need version-specific rendering, use {@link #getRenderer(String, String)} instead.
     * <p>
     * The dialect name is matched case-insensitively.
     *
     * @param dialect the name of the SQL dialect, must not be {@code null}
     * @return a result containing the renderer, or a failure if the dialect is not supported
     */
    public RegistryResult<SqlRenderer> getRenderer(String dialect) {
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
     * <b>Example:</b>
     * <pre>{@code
     * RegistryResult<SqlRenderer> result = registry.getRenderer("mysql", "8.0.35");
     *
     * SqlRenderer renderer = result.orElseThrow(); // Convert to exception if needed
     * }</pre>
     *
     * @param dialect the name of the SQL dialect, must not be {@code null}
     * @param version the database version (SemVer format), may be {@code null} to match any version
     * @return a result containing the renderer, or a failure if no matching plugin is found
     */
    public RegistryResult<SqlRenderer> getRenderer(String dialect, String version) {
        if (dialect == null) {
            return new Failure<>("Dialect name must not be null");
        }

        List<SqlDialectPlugin> dialectPlugins =
                plugins.getOrDefault(getNormalizedDialect(dialect), Collections.emptyList());

        if (version != null && !version.trim().isEmpty() && !SemVerUtil.isValidVersion(version)) {
            return new Failure<>("Invalid version format: '" + version + "'");
        }

        List<SqlDialectPlugin> matchingPlugins = findMatchingPlugins(dialectPlugins, version);

        if (matchingPlugins.isEmpty()) {
            String versionInfo = version != null ? " version '" + version + "'" : "";
            return new Failure<>("No plugin found for dialect '" + dialect + "'" + versionInfo
                    + ". Supported dialects: " + getSupportedDialects());
        }

        if (matchingPlugins.size() > 1) {
            logMultipleMatches(dialect, version, matchingPlugins);
        }

        return new Success<>(matchingPlugins.get(0).createRenderer());
    }

    /**
     * Finds all plugins matching the specified version.
     * <p>
     * This is a pure function: the result depends only on input parameters,
     * with no access to mutable state. This makes it highly testable and composable.
     * <p>
     * Package-private for testing purposes.
     *
     * @param availablePlugins the list of plugins to filter
     * @param version the version to match (SemVer format), or null to match any version
     * @return list of matching plugins, empty if none found
     * @throws IllegalArgumentException if the version format is invalid
     */
    static List<SqlDialectPlugin> findMatchingPlugins(List<SqlDialectPlugin> availablePlugins, String version) {
        if (availablePlugins.isEmpty()) {
            return Collections.emptyList();
        }

        if (version == null || version.trim().isEmpty()) {
            return availablePlugins;
        }

        if (!SemVerUtil.isValidVersion(version)) {
            throw new IllegalArgumentException("Invalid version format: '" + version + "'");
        }

        // Filter plugins by version compatibility
        // Note: plugin version ranges are already validated at registration time
        return availablePlugins.stream()
                .filter(plugin -> SemVerUtil.matches(version, plugin.dialectVersion()))
                .collect(Collectors.toList());
    }

    /**
     * Returns an immutable set of all supported SQL dialect names.
     * <p>
     * The returned set contains the canonical names of all registered dialects
     * in lowercase. The set is a snapshot and will not reflect changes to other
     * registry instances.
     *
     * @return an immutable set of supported dialect names, never {@code null}
     */
    public Set<String> getSupportedDialects() {
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
    public boolean isSupported(String dialect) {
        if (dialect == null) {
            return false;
        }

        return plugins.containsKey(getNormalizedDialect(dialect));
    }

    /**
     * Returns the total number of registered plugins across all dialects.
     *
     * @return the total number of plugins
     */
    public int size() {
        return plugins.values().stream().mapToInt(List::size).sum();
    }

    /**
     * Checks if this registry is empty (contains no plugins).
     *
     * @return {@code true} if no plugins are registered, {@code false} otherwise
     */
    public boolean isEmpty() {
        return plugins.isEmpty();
    }

    private static String getNormalizedDialect(String dialect) {
        return dialect.toLowerCase();
    }

    private void logMultipleMatches(String dialect, String version, List<SqlDialectPlugin> matchingPlugins) {
        String pluginInfo = matchingPlugins.stream()
                .map(p -> p.dialectName() + ":" + p.dialectVersion())
                .collect(Collectors.joining(", "));

        logger.warn(
                "Multiple plugins match dialect '{}' version '{}': [{}]. Using first match: {}",
                dialect,
                version,
                pluginInfo,
                matchingPlugins.get(0).dialectName() + ":"
                        + matchingPlugins.get(0).dialectVersion());
    }
}
