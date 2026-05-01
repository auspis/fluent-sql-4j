package io.github.auspis.fluentsql4j.hook.build;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BuildHookFactoryCompositeTest {

    @Test
    void emptyCompositeReturnsNullObject() {
        BuildHookFactory factory = BuildHookFactory.composite();

        assertThat(factory.create()).isSameAs(BuildHook.nullObject());
    }

    @Test
    void nullFactoriesAreIgnored() {
        BuildHookFactory factory = BuildHookFactory.composite(null, null);

        assertThat(factory.create()).isSameAs(BuildHook.nullObject());
    }

    @Test
    void nullObjectFactoriesAreIgnored() {
        BuildHookFactory factory =
                BuildHookFactory.composite(BuildHookFactory.nullObject(), BuildHookFactory.nullObject());

        assertThat(factory.create()).isSameAs(BuildHook.nullObject());
    }

    @Test
    void singleHook() {
        BuildHook hook = new TestBuildHook();
        BuildHookFactory factory =
                BuildHookFactory.composite(BuildHookFactory.nullObject(), () -> hook, BuildHookFactory.nullObject());

        BuildHook result = factory.create();

        assertThat(result).isInstanceOf(CompositeBuildHook.class);
    }

    @Test
    void multipleHooks() {
        BuildHook hook1 = new TestBuildHook();
        BuildHook hook2 = new TestBuildHook();
        BuildHookFactory factory = BuildHookFactory.composite(() -> hook1, () -> hook2);

        BuildHook result = factory.create();

        assertThat(result).isInstanceOf(CompositeBuildHook.class);
    }

    @Test
    void factoryIsCalledOnEveryCreate() {
        int[] callCount = {0};
        BuildHookFactory factory = BuildHookFactory.composite(() -> {
            callCount[0]++;
            return new TestBuildHook();
        });

        factory.create();
        factory.create();

        assertThat(callCount[0]).isEqualTo(2);
    }

    @Test
    void whenFactoriesReturnNull() {
        BuildHookFactory factory = BuildHookFactory.composite(() -> null);
        assertThat(factory.create()).isSameAs(BuildHook.nullObject());
    }

    @Test
    void whenFactoriesReturnNullObject() {
        BuildHookFactory factory = BuildHookFactory.composite(BuildHook::nullObject);
        assertThat(factory.create()).isSameAs(BuildHook.nullObject());
    }

    private static final class TestBuildHook extends BuildHook {}
}
