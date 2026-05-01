package io.github.auspis.fluentsql4j.hook.build;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.hook.build.logging.LoggingBuildHookProvider;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;

class BuildHookProviderServiceLoaderTest {

    @Test
    void discoversLoggingProvider() {
        ServiceLoader<BuildHookProvider> loader = ServiceLoader.load(BuildHookProvider.class);

        boolean found = StreamSupport.stream(loader.spliterator(), false)
                .anyMatch(provider -> provider.getClass().equals(LoggingBuildHookProvider.class));

        assertThat(found).isTrue();
    }
}
