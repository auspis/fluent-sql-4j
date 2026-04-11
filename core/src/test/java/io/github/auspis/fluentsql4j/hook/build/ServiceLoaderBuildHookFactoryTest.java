package io.github.auspis.fluentsql4j.hook.build;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ServiceLoaderBuildHookFactoryTest {

    private static final String TEST_ENABLED_KEY = "test.provider.enabled";

    @Test
    void propertiesSupplierNotCalledDuringConstruction() {
        AtomicInteger callCount = new AtomicInteger(0);

        ServiceLoaderBuildHookFactory factory = new ServiceLoaderBuildHookFactory(
                () -> {
                    callCount.incrementAndGet();
                    return new Properties();
                },
                List::of);

        assertThat(callCount.get()).isZero();

        factory.create();

        assertThat(callCount.get()).isOne();
    }

    @Test
    void propertiesResolvedAtCreateTime() {
        Properties props = new Properties();
        TestBuildHookProvider provider = new TestBuildHookProvider();
        provider.useProperties = true;

        ServiceLoaderBuildHookFactory factory = new ServiceLoaderBuildHookFactory(() -> props, () -> List.of(provider));

        props.setProperty(TEST_ENABLED_KEY, "true");

        BuildHook result = factory.create();

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
    void exception() {
        TestBuildHookProvider failingProvider = new TestBuildHookProvider();
        failingProvider.throwsExceptionOnConfigure = true;

        ServiceLoaderBuildHookFactory factory =
                new ServiceLoaderBuildHookFactory(Properties::new, () -> List.of(failingProvider));

        BuildHook result = factory.create();

        assertThat(result).isSameAs(BuildHook.nullObject());
    }

    @Test
    void providerThatReturnsNullHookIsIgnored() {
        TestBuildHookProvider provider = new TestBuildHookProvider();
        provider.buildHook = null;

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
        private BuildHook buildHook = BuildHook.nullObject();

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
}
