package io.github.auspis.fluentsql4j.ast.visitor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record AstContext(Set<Feature> features) {
    public enum Feature {
        WHERE,
        HAVING,
        GROUP_BY,
        JOIN_ON,
        UNION,
        SUBQUERY,
        WINDOW_FUNCTION,
        DDL
    }

    public AstContext(Feature... features) {
        this(Set.of(features));
    }

    public AstContext copy() {
        return new AstContext(Collections.unmodifiableSet(new HashSet<>(features)));
    }

    public AstContext withFeatures(Feature... newFeatures) {
        Set<Feature> combined = new HashSet<>(features);
        combined.addAll(Arrays.asList(newFeatures));
        return new AstContext(Collections.unmodifiableSet(combined));
    }

    public boolean hasFeature(Feature feature) {
        return features.contains(feature);
    }
}
