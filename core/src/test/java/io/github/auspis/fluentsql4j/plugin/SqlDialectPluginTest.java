package io.github.auspis.fluentsql4j.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.DSL;
import org.junit.jupiter.api.Test;

class SqlDialectPluginTest {

    @Test
    void shouldCreateValidPlugin() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", hookFactory -> new DSL(specFactory));

        assertThat(plugin.dialectName()).isEqualTo("mysql");
        assertThat(plugin.dialectVersion()).isEqualTo("^8.0.0");
        assertThat(plugin.createDSL(io.github.auspis.fluentsql4j.hook.build.BuildHookFactory.nullObject()))
                .isNotNull();
        assertThat(plugin.createDSL(io.github.auspis.fluentsql4j.hook.build.BuildHookFactory.nullObject())
                        .getSpecFactory())
                .isEqualTo(specFactory);
    }

    @Test
    void shouldRejectNullDialectName() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);

        assertThatThrownBy(() -> new SqlDialectPlugin(null, "^8.0.0", hookFactory -> new DSL(specFactory)))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Dialect name must not be null");
    }

    @Test
    void shouldRejectNullDialectVersion() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);

        assertThatThrownBy(() -> new SqlDialectPlugin("mysql", null, hookFactory -> new DSL(specFactory)))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Dialect version must not be null");
    }

    @Test
    void shouldRejectNullDslSupplier() {
        assertThatThrownBy(() -> new SqlDialectPlugin("mysql", "^8.0.0", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("DSL factory must not be null");
    }

    @Test
    void shouldRejectBlankVersionRange() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);

        assertThatThrownBy(() -> new SqlDialectPlugin("mysql", "", hookFactory -> new DSL(specFactory)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dialect version must not be blank")
                .hasMessageContaining("mysql");

        assertThatThrownBy(() -> new SqlDialectPlugin("mysql", "   ", hookFactory -> new DSL(specFactory)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dialect version must not be blank")
                .hasMessageContaining("mysql");
    }

    @Test
    void shouldAllowNonSemVerVersion() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);

        // Non-SemVer versions like "2008" should be allowed for exact matching
        SqlDialectPlugin plugin = new SqlDialectPlugin("standardsql", "2008", hookFactory -> new DSL(specFactory));

        assertThat(plugin.dialectName()).isEqualTo("standardsql");
        assertThat(plugin.dialectVersion()).isEqualTo("2008");
        assertThat(plugin.createDSL(io.github.auspis.fluentsql4j.hook.build.BuildHookFactory.nullObject())
                        .getSpecFactory())
                .isEqualTo(specFactory);
    }

    @Test
    void shouldAllowArbitraryVersionString() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);

        // Arbitrary version strings should be allowed
        SqlDialectPlugin plugin1 = new SqlDialectPlugin("customdb", "2011", hookFactory -> new DSL(specFactory));
        SqlDialectPlugin plugin2 = new SqlDialectPlugin("customdb", "v1", hookFactory -> new DSL(specFactory));
        SqlDialectPlugin plugin3 = new SqlDialectPlugin("customdb", "latest", hookFactory -> new DSL(specFactory));

        assertThat(plugin1.dialectVersion()).isEqualTo("2011");
        assertThat(plugin2.dialectVersion()).isEqualTo("v1");
        assertThat(plugin3.dialectVersion()).isEqualTo("latest");
    }

    @Test
    void shouldHaveValueSemantics() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        java.util.function.Function<io.github.auspis.fluentsql4j.hook.build.BuildHookFactory, DSL> dslFactory =
                hookFactory -> new DSL(specFactory);

        SqlDialectPlugin plugin1 = new SqlDialectPlugin("mysql", "^8.0.0", dslFactory);
        SqlDialectPlugin plugin2 = new SqlDialectPlugin("mysql", "^8.0.0", dslFactory);

        // Value semantics: equals based on field values (same factory instance)
        assertThat(plugin1).isEqualTo(plugin2);
        assertThat(plugin1.hashCode()).isEqualTo(plugin2.hashCode());
    }

    @Test
    void shouldBeImmutable() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", hookFactory -> new DSL(specFactory));

        // All fields are final by record definition
        assertThat(plugin.dialectName()).isEqualTo("mysql");
        assertThat(plugin.dialectVersion()).isEqualTo("^8.0.0");

        // Cannot mutate - compiler enforces this
        // No setters exist on records
    }

    @Test
    void shouldSupportMethodReferences() {
        // Record works well with functional programming
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);

        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", hookFactory -> new DSL(specFactory));

        DSL dsl1 = plugin.createDSL(io.github.auspis.fluentsql4j.hook.build.BuildHookFactory.nullObject());
        DSL dsl2 = plugin.createDSL(io.github.auspis.fluentsql4j.hook.build.BuildHookFactory.nullObject());

        assertThat(dsl1.getSpecFactory()).isEqualTo(specFactory);
        assertThat(dsl2.getSpecFactory()).isEqualTo(specFactory);
    }

    @Test
    void shouldHaveToString() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", hookFactory -> new DSL(specFactory));

        String toString = plugin.toString();

        assertThat(toString).contains("mysql");
        assertThat(toString).contains("^8.0.0");
        assertThat(toString).contains("SqlDialectPlugin");
    }

    @Test
    void shouldCreateDSL() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", hookFactory -> new DSL(specFactory));

        DSL dsl = plugin.createDSL(io.github.auspis.fluentsql4j.hook.build.BuildHookFactory.nullObject());

        assertThat(dsl).isNotNull();
        assertThat(dsl.getSpecFactory()).isEqualTo(specFactory);
    }

    @Test
    void shouldCreateNewDSLOnEachCall() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", hookFactory -> new DSL(specFactory));

        DSL dsl1 = plugin.createDSL(io.github.auspis.fluentsql4j.hook.build.BuildHookFactory.nullObject());
        DSL dsl2 = plugin.createDSL(io.github.auspis.fluentsql4j.hook.build.BuildHookFactory.nullObject());

        assertThat(dsl1).isNotSameAs(dsl2);
    }
}
