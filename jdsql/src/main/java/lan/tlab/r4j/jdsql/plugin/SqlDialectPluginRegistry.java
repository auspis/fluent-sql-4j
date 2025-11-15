package lan.tlab.r4j.jdsql.plugin;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.functional.Result;
import lan.tlab.r4j.jdsql.functional.Result.Failure;
import lan.tlab.r4j.jdsql.functional.Result.Success;
import lan.tlab.r4j.jdsql.plugin.util.SemVerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Immutable registry for SQL dialect plugins.
 * <p>
 * This registry follows functional programming principles:
 * <ul>
 *   <li><b>Immutability</b>: Once created, a registry instance cannot be modified</li>
 *   <li><b>Pure functions</b>: Query methods have no side effects</li>
 *   <li><b>No exceptions</b>: Operations return {@link lan.tlab.r4j.jdsql.functional.Result} instead of throwing</li>
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
 * Result<SqlRenderer> result = registry.getRenderer("mysql", "8.0.35");
 *
 * switch (result) {
 *     case Success<SqlRenderer>(SqlRenderer renderer) -> // use renderer
 * import lan.tlab.r4j.sql.functional.Result;
 * import lan.tlab.r4j.sql.functional.Result.Failure;
 * import lan.tlab.r4j.sql.functional.Result.Success;
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
 * @see lan.tlab.r4j.jdsql.functional.Result
 * @since 1.0
 */
public final class SqlDialectPluginRegistry {

    private static final Logger logger = LoggerFactory.getLogger(SqlDialectPluginRegistry.class);

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
    private SqlDialectPluginRegistry(Map<String, List<SqlDialectPlugin>> plugins) {
        this.plugins = plugins.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, entry -> List.copyOf(entry.getValue())));
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
    public static SqlDialectPluginRegistry createWithServiceLoader() {
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
    public static SqlDialectPluginRegistry empty() {
        return new SqlDialectPluginRegistry(Map.of());
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
    public static SqlDialectPluginRegistry of(List<SqlDialectPlugin> pluginList) {
        Objects.requireNonNull(pluginList, "Plugins list must not be null");

        Map<String, List<SqlDialectPlugin>> pluginMap = pluginList.stream()
                .peek(plugin -> Objects.requireNonNull(plugin, "Plugin list must not contain null elements"))
                .collect(Collectors.groupingBy(
                        plugin -> getNormalizedDialect(plugin.dialectName()), LinkedHashMap::new, Collectors.toList()));

        return new SqlDialectPluginRegistry(pluginMap);
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
    public SqlDialectPluginRegistry register(SqlDialectPlugin plugin) {
        Objects.requireNonNull(plugin, "Plugin must not be null");

        List<SqlDialectPlugin> allPlugins = Stream.concat(
                        this.plugins.values().stream().flatMap(List::stream), Stream.of(plugin))
                .toList();

        return of(allPlugins);
    }

    /**
     * Retrieves a {@link DialectRenderer} for the specified SQL dialect without version matching.
     * <p>
     * This method returns the first available plugin for the given dialect, regardless of version.
     * If you need version-specific rendering, use {@link #getDialectRenderer(String, String)} instead.
     * <p>
     * The dialect name is matched case-insensitively.
     *
     * @param dialect the name of the SQL dialect, must not be {@code null}
     * @return a result containing the renderer, or a failure if the dialect is not supported
     */
    public Result<DialectRenderer> getRenderer(String dialect) {
        return getDialectRenderer(dialect, null);
    }

    /**
     * Retrieves a {@link DialectRenderer} for the specified SQL dialect and version.
     * <p>
     * The renderer encapsulates both SQL and PreparedStatement rendering,
     * ensuring consistency between SQL generation and prepared statement creation.
     * <p>
     * The dialect name is matched case-insensitively. The registry finds all plugins
     * registered for the given dialect and filters them by version compatibility.
     * <p>
     * Version matching strategy:
     * <ul>
     *   <li>If the requested version is SemVer-compatible, uses SemVer range matching</li>
     *   <li>If the requested version is not SemVer-compatible, uses exact string matching</li>
     * </ul>
     * <p>
     * If multiple plugins match the requested version, the first match is used and a warning
     * is logged. This typically indicates overlapping version ranges in plugin configuration.
     * <p>
     * <b>Example with SemVer:</b>
     * <pre>{@code
     * Result<DialectRenderer> result = registry.getRenderer("mysql", "8.0.35");
     * }</pre>
     * <p>
     * <b>Example with non-SemVer:</b>
     * <pre>{@code
     * Result<DialectRenderer> result = registry.getRenderer("standardsql", "2008");
     * }</pre>
     *
     * @param dialect the name of the SQL dialect, must not be {@code null}
     * @param version the database version, may be {@code null} to match any version
     * @return a result containing the renderer, or a failure if no matching plugin is found
     */
    public Result<DialectRenderer> getDialectRenderer(String dialect, String version) {
        if (dialect == null) {
            return new Failure<>("Dialect name must not be null");
        }

        List<SqlDialectPlugin> dialectPlugins =
                plugins.getOrDefault(getNormalizedDialect(dialect), Collections.emptyList());

        List<SqlDialectPlugin> matchingPlugins = findMatchingPlugins(dialectPlugins, version);

        if (matchingPlugins.isEmpty()) {
            String versionInfo = version != null ? " version '" + version + "'" : "";
            return new Failure<>("No plugin found for dialect '" + dialect + "'" + versionInfo
                    + ". Supported dialects: " + getSupportedDialects());
        }

        if (matchingPlugins.size() > 1) {
            logMultipleMatches(dialect, version, matchingPlugins);
        }

        return new Success<>(matchingPlugins.get(0).createDSL().getRenderer());
    }

    /**
     * Gets a {@link SqlRenderer} for the specified dialect and version.
     * <p>
     * This is a convenience method that extracts the SQL renderer from the dialect renderer.
     *
     * @param dialect the dialect name
     * @param version the version to match
     * @return a {@link lan.tlab.r4j.jdsql.functional.Result} containing either the SQL renderer or an error message
     */
    public Result<SqlRenderer> getSqlRenderer(String dialect, String version) {
        return getDialectRenderer(dialect, version).map(DialectRenderer::sqlRenderer);
    }

    /**
     * Gets a {@link PreparedStatementRenderer} for the specified dialect and version.
     * <p>
     * This is a convenience method that extracts the PS renderer from the dialect renderer.
     *
     * @param dialect the dialect name
     * @param version the version to match
     * @return a {@link lan.tlab.r4j.jdsql.functional.Result} containing either the PS renderer or an error message
     */
    public Result<PreparedStatementRenderer> getPsRenderer(String dialect, String version) {
        return getDialectRenderer(dialect, version).map(DialectRenderer::psRenderer);
    }

    /**
     * Retrieves a {@link lan.tlab.r4j.jdsql.dsl.DSL} instance for the specified SQL dialect and version.
     * <p>
     * This method returns the DSL instance created by the plugin's {@code createDSL()} method.
     * This allows dialect-specific DSL extensions (like {@link lan.tlab.r4j.sql.dsl.mysql.MySQLDSL})
     * to be properly returned to callers.
     * <p>
     * The dialect name is matched case-insensitively. The registry finds all plugins
     * registered for the given dialect and filters them by version compatibility.
     * <p>
     * Version matching strategy:
     * <ul>
     *   <li>If the requested version is SemVer-compatible, uses SemVer range matching</li>
     *   <li>If the requested version is not SemVer-compatible, uses exact string matching</li>
     * </ul>
     * <p>
     * If multiple plugins match the requested version, the first match is used and a warning
     * is logged.
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * Result<DSL> result = registry.getDialect("mysql", "8.0.35");
     * // Returns a MySQLDSL instance, not a base DSL
     * }</pre>
     *
     * @param dialect the name of the SQL dialect, must not be {@code null}
     * @param version the database version, may be {@code null} to match any version
     * @return a result containing the DSL instance, or a failure if no matching plugin is found
     */
    public Result<DSL> getDsl(String dialect, String version) {
        if (dialect == null) {
            return new Failure<>("Dialect name must not be null");
        }

        List<SqlDialectPlugin> dialectPlugins =
                plugins.getOrDefault(getNormalizedDialect(dialect), Collections.emptyList());

        List<SqlDialectPlugin> matchingPlugins = findMatchingPlugins(dialectPlugins, version);

        if (matchingPlugins.isEmpty()) {
            String versionInfo = version != null ? " version '" + version + "'" : "";
            return new Failure<>("No plugin found for dialect '" + dialect + "'" + versionInfo
                    + ". Supported dialects: " + getSupportedDialects());
        }

        if (matchingPlugins.size() > 1) {
            logMultipleMatches(dialect, version, matchingPlugins);
        }

        return new Success<>(matchingPlugins.get(0).createDSL());
    }

    /**
     * Finds all plugins matching the specified version.
     * <p>
     * This is a pure function: the result depends only on input parameters,
     * with no access to mutable state. This makes it highly testable and composable.
     * <p>
     * Matching strategy:
     * <ul>
     *   <li>If version is null or empty, returns all available plugins</li>
     *   <li>If version is SemVer-compatible, uses SemVer range matching</li>
     *   <li>If version is not SemVer-compatible, uses exact string matching (case-sensitive)</li>
     * </ul>
     * <p>
     * Package-private for testing purposes.
     *
     * @param availablePlugins the list of plugins to filter
     * @param version the version to match, or null to match any version
     * @return list of matching plugins, empty if none found
     */
    static List<SqlDialectPlugin> findMatchingPlugins(List<SqlDialectPlugin> availablePlugins, String version) {
        if (availablePlugins.isEmpty()) {
            return Collections.emptyList();
        }

        if (version == null || version.trim().isEmpty()) {
            return availablePlugins;
        }

        boolean isSemVer = SemVerUtil.isValidVersion(version);

        return availablePlugins.stream()
                .filter(plugin -> matchesVersion(version, plugin.dialectVersion(), isSemVer))
                .toList();
    }

    /**
     * Determines if a requested version matches a plugin's version specification.
     * <p>
     * Uses different matching strategies based on whether the requested version is SemVer-compatible:
     * <ul>
     *   <li>For SemVer versions: uses SemVer range matching (e.g., "8.0.35" matches "^8.0.0")</li>
     *   <li>For non-SemVer versions: uses exact string matching (e.g., "2008" matches "2008")</li>
     * </ul>
     * <p>
     * Package-private for testing purposes.
     *
     * @param requestedVersion the version requested by the user
     * @param pluginVersion the version specification from the plugin
     * @param isSemVer whether the requested version is SemVer-compatible
     * @return {@code true} if the versions match, {@code false} otherwise
     */
    static boolean matchesVersion(String requestedVersion, String pluginVersion, boolean isSemVer) {
        if (isSemVer && SemVerUtil.isValidRange(pluginVersion)) {
            // Both are SemVer-compatible: use SemVer matching
            try {
                return SemVerUtil.matches(requestedVersion, pluginVersion);
            } catch (IllegalArgumentException e) {
                // SemVer matching failed, fall through to exact match
                return false;
            }
        } else {
            // Non-SemVer version: use exact string matching
            return requestedVersion.equals(pluginVersion);
        }
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
