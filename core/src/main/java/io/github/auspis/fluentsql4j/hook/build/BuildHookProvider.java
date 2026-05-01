package io.github.auspis.fluentsql4j.hook.build;

import java.util.Properties;

/**
 * Service provider contract for build hooks.
 *
 * <p>Implementations can be discovered via {@link java.util.ServiceLoader} (SPI path) or
 * instantiated programmatically. In both cases, all configuration must be in place before
 * {@link #isEnabled()} and {@link #create()} are called.
 *
 * <p>For SPI-discovered providers, {@link #configure(Properties)} is called once by the
 * factory immediately after instantiation, allowing properties-based configuration.
 *
 * <p>For programmatic providers, configuration belongs in the constructor — {@link
 * #configure(Properties)} can be left as a no-op.
 */
public interface BuildHookProvider {

    String id();

    int order();

    boolean isEnabled();

    BuildHook create();

    /**
     * Configures this provider from the supplied properties.
     *
     * <p>Called once by the factory before {@link #isEnabled()} and {@link #create()}.
     * The default implementation is a no-op, suitable for programmatic providers whose
     * configuration lives entirely in the constructor.
     *
     * @param properties the configuration source
     * @return this instance (for method chaining)
     */
    default BuildHookProvider configure(Properties properties) {
        return this;
    }
}
