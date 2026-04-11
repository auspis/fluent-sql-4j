package io.github.auspis.fluentsql4j.hook.build;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@FunctionalInterface
public interface BuildHookFactory {

    static final BuildHookFactory NULL_OBJECT_INSTANCE = BuildHook::nullObject;

    BuildHook create();

    static BuildHookFactory nullObject() {
        return NULL_OBJECT_INSTANCE;
    }

    /**
     * Returns a factory that composes the results of all provided factories.
     *
     * <p>Null factories and factories that produce the null-object hook are silently ignored.
     * If only one real hook remains, it is returned directly (no composite wrapper).
     *
     * @param factories the factories to compose; must not be {@code null}
     * @return a composing factory, never {@code null}
     */
    static BuildHookFactory composite(BuildHookFactory... factories) {
        Objects.requireNonNull(factories, "factories must not be null");
        return () -> {
            List<BuildHook> hooks = Arrays.stream(factories)
                    .filter(Objects::nonNull)
                    .map(BuildHookFactory::create)
                    .filter(hook -> hook != null && hook != BuildHook.nullObject())
                    .toList();
            if (hooks.isEmpty()) {
                return BuildHook.nullObject();
            }
            if (hooks.size() == 1) {
                return hooks.get(0);
            }
            return new CompositeBuildHook(hooks);
        };
    }
}
