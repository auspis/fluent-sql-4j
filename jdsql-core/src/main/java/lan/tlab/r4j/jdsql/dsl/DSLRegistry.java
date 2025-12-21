package lan.tlab.r4j.jdsql.dsl;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lan.tlab.r4j.jdsql.functional.Result;
import lan.tlab.r4j.jdsql.functional.Result.Failure;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPluginRegistry;

/**
 * Registry for managing dialect-specific DSL instances.
 * <p>
 * This class provides a simplified API for creating DSL instances configured
 * for specific database dialects. It wraps {@link SqlDialectPluginRegistry} and
 * provides a clean separation of concerns between plugin management and DSL usage.
 * <p>
 * <b>Creating a registry:</b>
 * <pre>{@code
 * // Load plugins via ServiceLoader
 * DSLRegistry registry = DSLRegistry.createWithServiceLoader();
 *
 * // Create with custom plugin registry
 * SqlDialectPluginRegistry pluginRegistry = SqlDialectPluginRegistry.of(List.of(plugin));
 * DSLRegistry registry = DSLRegistry.of(pluginRegistry);
 * }</pre>
 * <p>
 * <b>Using the registry:</b>
 * <pre>{@code
 * DSLRegistry registry = DSLRegistry.createWithServiceLoader();
 * Result<DSL> result = registry.dslFor("mysql", "8.0.35");
 *
 * // Use pattern matching
 * switch (result) {
 *     case Success<DSL>(DSL dsl) -> {
 *         String sql = dsl.select("name").from("users").build();
 *     }
 *     case Failure<DSL> f -> System.err.println(f.message());
 * }
 *
 * // Or use helper methods
 * DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();
 * String sql = dsl.select("name").from("users").build();
 * }</pre>
 * <p>
 * <b>Benefits:</b>
 * <ul>
 *   <li><b>Separation of Concerns</b>: Plugin management is separated from DSL usage</li>
 *   <li><b>Simpler API</b>: Users work with DSL directly, not with astToPsSpecVisitor</li>
 *   <li><b>Caching</b>: DSL instances are cached per dialect/version for efficiency</li>
 *   <li><b>Type Safety</b>: Returns {@link Result} for explicit error handling</li>
 *   <li><b>Testability</b>: Easy to mock and test</li>
 * </ul>
 *
 * @see DSL
 * @see SqlDialectPluginRegistry
 * @see Result
 * @since 1.0
 */
public final class DSLRegistry {

    private final SqlDialectPluginRegistry pluginRegistry;
    private final Map<String, DSL> dslCache;

    /**
     * Private constructor. Use factory methods to create instances.
     *
     * @param pluginRegistry the plugin registry to use for dialect resolution
     */
    private DSLRegistry(SqlDialectPluginRegistry pluginRegistry) {
        this.pluginRegistry = Objects.requireNonNull(pluginRegistry, "SqlDialectPluginRegistry must not be null");
        dslCache = new ConcurrentHashMap<>();
    }

    /**
     * Creates a registry with plugins discovered via {@link java.util.ServiceLoader}.
     * <p>
     * This is the recommended way to create a registry in production code.
     * It automatically discovers and loads all SQL dialect plugins available
     * on the classpath.
     *
     * @return a new registry instance with all discovered plugins
     */
    public static DSLRegistry createWithServiceLoader() {
        return new DSLRegistry(SqlDialectPluginRegistry.createWithServiceLoader());
    }

    /**
     * Creates a registry with the specified plugin registry.
     * <p>
     * This is useful for testing or when you need more control over plugin loading.
     *
     * @param pluginRegistry the plugin registry to use
     * @return a new registry instance
     * @throws NullPointerException if {@code pluginRegistry} is {@code null}
     */
    public static DSLRegistry of(SqlDialectPluginRegistry pluginRegistry) {
        return new DSLRegistry(pluginRegistry);
    }

    /**
     * Creates a DSL instance for the specified SQL dialect without version matching.
     * <p>
     * This method returns a DSL configured for the first available plugin for the
     * given dialect, regardless of version. If you need version-specific behavior,
     * use {@link #dslFor(String, String)} instead.
     * <p>
     * The dialect name is matched case-insensitively.
     *
     * @param dialect the name of the SQL dialect, must not be {@code null}
     * @return a result containing the DSL instance, or a failure if the dialect is not supported
     */
    public Result<DSL> dslFor(String dialect) {
        return dslFor(dialect, "");
    }

    /**
     * Creates a DSL instance for the specified SQL dialect and version.
     * <p>
     * DSL instances are cached per dialect/version combination. Multiple calls
     * with the same parameters will return the same DSL instance, following
     * the Registry pattern convention.
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
     * <b>Example with SemVer:</b>
     * <pre>{@code
     * Result<DSL> result = registry.dslFor("mysql", "8.0.35");
     * }</pre>
     * <p>
     * <b>Example with non-SemVer:</b>
     * <pre>{@code
     * Result<DSL> result = registry.dslFor("standardsql", "2008");
     * }</pre>
     *
     * @param dialect the name of the SQL dialect, must not be {@code null}
     * @param version the database version, may be {@code null} to match any version
     * @return a result containing the DSL instance, or a failure if no matching plugin is found
     */
    public Result<DSL> dslFor(String dialect, String version) {
        String cacheKey = buildCacheKey(dialect, version);

        // Check cache first
        DSL cachedDsl = dslCache.get(cacheKey);
        if (cachedDsl != null) {
            return new Result.Success<>(cachedDsl);
        }

        // Not in cache, get DSL from plugin and cache it
        return pluginRegistry.getDsl(dialect, version).map(dsl -> {
            dslCache.put(cacheKey, dsl);
            return dsl;
        });
    }

    public <T extends DSL> Result<T> dslFor(String dialect, Class<T> dslClass) {
        return dslFor(dialect)
                .fold(
                        dsl -> dslClass.isInstance(dsl)
                                ? new Result.Success<>(dslClass.cast(dsl))
                                : new Result.Failure<>("No plugin found"),
                        Failure::new);
    }

    public <T extends DSL> Result<T> dslFor(String dialect, String version, Class<T> dslClass) {
        return dslFor(dialect, version)
                .fold(
                        dsl -> dslClass.isInstance(dsl)
                                ? new Result.Success<>(dslClass.cast(dsl))
                                : new Result.Failure<>("No plugin found"),
                        Failure::new);
    }

    public Set<String> getSupportedDialects() {
        return pluginRegistry.getSupportedDialects();
    }

    public boolean isSupported(String dialect) {
        return pluginRegistry.isSupported(dialect);
    }

    public void clearCache() {
        dslCache.clear();
    }

    public int getCacheSize() {
        return dslCache.size();
    }

    private String buildCacheKey(String dialect, String version) {
        String normalizedDialect = isNotBlank(dialect) ? dialect.toLowerCase() : "null";
        String normalizedVersion = isNotBlank(version) ? version : "*";
        return normalizedDialect + ":" + normalizedVersion;
    }

    private boolean isNotBlank(String dialect) {
        return (dialect != null) && !dialect.isBlank();
    }
}
