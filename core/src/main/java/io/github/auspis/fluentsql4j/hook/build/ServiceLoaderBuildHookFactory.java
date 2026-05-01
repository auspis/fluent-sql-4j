package io.github.auspis.fluentsql4j.hook.build;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServiceLoaderBuildHookFactory implements BuildHookFactory {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLoaderBuildHookFactory.class);

    private final Supplier<Properties> propertiesSupplier;
    private final List<BuildHookProvider> cachedProviders;

    public ServiceLoaderBuildHookFactory() {
        this(System::getProperties);
    }

    ServiceLoaderBuildHookFactory(Supplier<Properties> propertiesSupplier) {
        this.propertiesSupplier = propertiesSupplier;
        // Load and configure providers ONCE in constructor
        Properties initialProps = propertiesSupplier.get();
        this.cachedProviders = loadAndConfigureProviders(initialProps);
    }

    // Package-private constructor for testing: inject providers instead of using ServiceLoader
    ServiceLoaderBuildHookFactory(
            Supplier<Properties> propertiesSupplier, Supplier<List<BuildHookProvider>> providersSupplier) {
        this.propertiesSupplier = propertiesSupplier;
        // Load and configure provided providers ONCE in constructor
        Properties initialProps = propertiesSupplier.get();
        List<BuildHookProvider> loaded = new ArrayList<>();
        for (BuildHookProvider provider : providersSupplier.get()) {
            try {
                provider.configure(initialProps);
                loaded.add(provider);
            } catch (Throwable t) {
                logger.warn("Skipping build hook provider '{}' due to error during configuration", provider.id(), t);
            }
        }
        this.cachedProviders = loaded.stream()
                .sorted(Comparator.comparingInt(BuildHookProvider::order))
                .toList();
    }

    @Override
    public BuildHook create() {
        List<BuildHook> hooks = new ArrayList<>();

        for (BuildHookProvider provider : cachedProviders) {
            try {
                if (!provider.isEnabled()) {
                    continue;
                }
                BuildHook hook = provider.create();
                if (!BuildHook.isNull(hook)) {
                    hooks.add(hook);
                }
            } catch (Throwable t) {
                logger.warn("Skipping build hook provider '{}' due to error", provider.id(), t);
            }
        }

        if (hooks.isEmpty()) {
            return BuildHook.nullObject();
        }

        return new CompositeBuildHook(hooks);
    }

    private static List<BuildHookProvider> loadAndConfigureProviders(Properties properties) {
        ServiceLoader<BuildHookProvider> loader = ServiceLoader.load(BuildHookProvider.class);
        List<BuildHookProvider> loaded = new ArrayList<>();
        for (BuildHookProvider provider : loader) {
            try {
                provider.configure(properties);
                loaded.add(provider);
            } catch (Throwable t) {
                logger.warn("Skipping build hook provider '{}' due to error during configuration", provider.id(), t);
            }
        }
        return loaded.stream()
                .sorted(Comparator.comparingInt(BuildHookProvider::order))
                .toList();
    }
}
