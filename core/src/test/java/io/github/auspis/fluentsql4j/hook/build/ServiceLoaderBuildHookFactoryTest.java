package io.github.auspis.fluentsql4j.hook.build;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ServiceLoaderBuildHookFactoryTest {

    private static final String TEST_ENABLED_KEY = "test.provider.enabled";

    @Test
    void providersLoadedAndConfiguredDuringConstruction() {
        AtomicInteger configureCallCount = new AtomicInteger(0);
        TestBuildHookProvider provider = new TestBuildHookProvider() {
            @Override
            public BuildHookProvider configure(Properties properties) {
                configureCallCount.incrementAndGet();
                return super.configure(properties);
            }
        };

        new ServiceLoaderBuildHookFactory(Properties::new, () -> List.of(provider));

        // configure() should be called during construction
        assertThat(configureCallCount.get()).isOne();
    }

    @Test
    void providersNotReconfiguredOnCreate() {
        AtomicInteger configureCallCount = new AtomicInteger(0);
        TestBuildHookProvider provider = new TestBuildHookProvider() {
            @Override
            public BuildHookProvider configure(Properties properties) {
                configureCallCount.incrementAndGet();
                return super.configure(properties);
            }
        };
        provider.enabled = true;

        ServiceLoaderBuildHookFactory factory =
                new ServiceLoaderBuildHookFactory(Properties::new, () -> List.of(provider));

        // After construction, configureCallCount = 1
        assertThat(configureCallCount.get()).isOne();

        // Calling create() multiple times should NOT increase configureCallCount
        factory.create();
        factory.create();

        assertThat(configureCallCount.get()).isOne();
    }

    @Test
    void propertiesMustBeSetBeforeConstruction() {
        Properties props = new Properties();
        TestBuildHookProvider provider = new TestBuildHookProvider();
        provider.useProperties = true;

        // Properties must be set BEFORE factory construction
        props.setProperty(TEST_ENABLED_KEY, "true");

        ServiceLoaderBuildHookFactory factory = new ServiceLoaderBuildHookFactory(() -> props, () -> List.of(provider));

        BuildHook result = factory.create();

        // Provider should be enabled because properties were set before construction
        assertThat(result).isNotSameAs(BuildHook.nullObject());
    }

    @Test
    void disabled() {
        ServiceLoaderBuildHookFactory factory =
                new ServiceLoaderBuildHookFactory(Properties::new, () -> List.of(new TestBuildHookProvider()));

        BuildHook result = factory.create();

        assertThat(result).isSameAs(BuildHook.nullObject());
    }

    @Test
    void enabled() {
        TestBuildHookProvider provider = new TestBuildHookProvider();
        provider.enabled = true;
        ServiceLoaderBuildHookFactory factory =
                new ServiceLoaderBuildHookFactory(Properties::new, () -> List.of(provider));

        BuildHook result = factory.create();
        assertThat(result).isNotSameAs(BuildHook.nullObject());
    }

    @Test
    void exceptionDuringConfigurationSkipsProvider() {
        TestBuildHookProvider failingProvider = new TestBuildHookProvider();
        failingProvider.throwsExceptionOnConfigure = true;

        // Exception during construction is logged but doesn't propagate
        ServiceLoaderBuildHookFactory factory =
                new ServiceLoaderBuildHookFactory(Properties::new, () -> List.of(failingProvider));

        BuildHook result = factory.create();

        // Failing provider is skipped, so result is null object
        assertThat(result).isSameAs(BuildHook.nullObject());
    }

    @Test
    void providerThatReturnsNullHookIsIgnored() {
        TestBuildHookProvider provider = new TestBuildHookProvider();
        provider.enabled = true;
        provider.buildHook = null;

        ServiceLoaderBuildHookFactory factory =
                new ServiceLoaderBuildHookFactory(Properties::new, () -> List.of(provider));

        BuildHook result = factory.create();

        assertThat(result).isSameAs(BuildHook.nullObject());
    }

    @Test
    void providerThatReturnsNullObjectHookIsIgnored() {
        TestBuildHookProvider provider = new TestBuildHookProvider();
        provider.enabled = true;
        provider.buildHook = BuildHook.nullObject();

        ServiceLoaderBuildHookFactory factory =
                new ServiceLoaderBuildHookFactory(Properties::new, () -> List.of(provider));

        BuildHook result = factory.create();

        assertThat(result).isSameAs(BuildHook.nullObject());
    }

    private static class TestBuildHookProvider implements BuildHookProvider {
        private boolean enabled = false;
        private String id = "test";
        private int order = 0;
        private boolean useProperties = false;
        private boolean throwsExceptionOnConfigure = false;
        private BuildHook buildHook = new TestBuildHook();

        @Override
        public String id() {
            return id;
        }

        @Override
        public int order() {
            return order;
        }

        @Override
        public BuildHookProvider configure(Properties properties) {
            if (throwsExceptionOnConfigure) {
                throw new RuntimeException("provider-fail");
            }
            if (useProperties) {
                enabled = Boolean.parseBoolean(properties.getProperty(TEST_ENABLED_KEY, "false"));
            }
            return this;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public BuildHook create() {
            return buildHook;
        }
    }

    private static final class TestBuildHook extends BuildHook {}
}
