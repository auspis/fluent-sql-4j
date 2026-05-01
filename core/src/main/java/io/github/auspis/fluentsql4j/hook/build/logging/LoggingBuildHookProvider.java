package io.github.auspis.fluentsql4j.hook.build.logging;

import io.github.auspis.fluentsql4j.hook.build.BuildHook;
import io.github.auspis.fluentsql4j.hook.build.BuildHookProvider;
import java.util.Locale;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public final class LoggingBuildHookProvider implements BuildHookProvider {

    public static final String ENABLED_PROPERTY = "fluentsql.hooks.build.logging.enabled";
    public static final String INCLUDE_PARAMS_PROPERTY = "fluentsql.hooks.build.logging.includeParams";
    public static final String LEVEL_PROPERTY = "fluentsql.hooks.build.logging.level";

    private static final int DEFAULT_ORDER = 1000;

    private Logger logger;
    private Level level;
    private boolean includeParams;
    private boolean enabled;

    /**
     * No-arg constructor for {@link java.util.ServiceLoader} discovery.
     *
     * <p>The provider starts as disabled with DEBUG level and no parameter logging.
     * Call {@link #configure(Properties)} to apply properties-based configuration.
     */
    public LoggingBuildHookProvider() {
        this(LoggerFactory.getLogger(LoggingBuildHook.class), Level.DEBUG, false, false);
    }

    /**
     * Programmatic constructor: fully configures the provider at construction time.
     *
     * <p>No call to {@link #configure(Properties)} is needed when using this constructor.
     *
     * @param logger the logger instance to use
     * @param level the log level
     * @param includeParams whether to include bound parameters in log output
     * @param enabled whether this provider is active
     */
    public LoggingBuildHookProvider(Logger logger, Level level, boolean includeParams, boolean enabled) {
        this.logger = logger;
        this.level = level;
        this.includeParams = includeParams;
        this.enabled = enabled;
    }

    @Override
    public String id() {
        return "fluentsql.hooks.build.logging";
    }

    @Override
    public int order() {
        return DEFAULT_ORDER;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public BuildHook create() {
        return new LoggingBuildHook(logger, level, includeParams);
    }

    /**
     * Configures this provider from the supplied properties.
     *
     * <p>Reads {@link #ENABLED_PROPERTY}, {@link #LEVEL_PROPERTY} and {@link
     * #INCLUDE_PARAMS_PROPERTY}. Called automatically by the SPI factory; not needed when using
     * the programmatic constructor.
     */
    @Override
    public LoggingBuildHookProvider configure(Properties properties) {
        this.enabled = Boolean.parseBoolean(properties.getProperty(ENABLED_PROPERTY, "false"));
        this.level = parseLevel(properties.getProperty(LEVEL_PROPERTY, "DEBUG"));
        this.includeParams = Boolean.parseBoolean(properties.getProperty(INCLUDE_PARAMS_PROPERTY, "false"));
        return this;
    }

    private static Level parseLevel(String rawLevel) {
        String normalized = rawLevel.trim().toUpperCase(Locale.ROOT);
        try {
            return Level.valueOf(normalized);
        } catch (IllegalArgumentException ignored) {
            return Level.DEBUG;
        }
    }
}
