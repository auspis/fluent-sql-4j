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

    private final Supplier<List<BuildHookProvider>> providersSupplier;
    private final Supplier<Properties> propertiesSupplier;

    public ServiceLoaderBuildHookFactory() {
        this(System::getProperties, ServiceLoaderBuildHookFactory::loadProviders);
    }

    ServiceLoaderBuildHookFactory(
            Supplier<Properties> propertiesSupplier, Supplier<List<BuildHookProvider>> providersSupplier) {
        this.propertiesSupplier = propertiesSupplier;
        this.providersSupplier = providersSupplier;
    }

    @Override
    public BuildHook create() {
        List<BuildHook> hooks = new ArrayList<>();

        Properties properties = propertiesSupplier.get();
        for (BuildHookProvider provider : providersSupplier.get()) {
            try {
                provider.configure(properties);
                if (!provider.isEnabled()) {
                    continue;
                }
                BuildHook hook = provider.create();
                if (hook != null) {
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

    private static List<BuildHookProvider> loadProviders() {
        ServiceLoader<BuildHookProvider> loader = ServiceLoader.load(BuildHookProvider.class);
        return loader.stream()
                .map(ServiceLoader.Provider::get)
                .sorted(Comparator.comparingInt(BuildHookProvider::order))
                .toList();
    }
}
