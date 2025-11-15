package lan.tlab.r4j.jdsql.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import org.junit.jupiter.api.Test;

class SqlDialectPluginTest {

    @Test
    void shouldCreateValidPlugin() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", () -> new DSL(renderer));

        assertThat(plugin.dialectName()).isEqualTo("mysql");
        assertThat(plugin.dialectVersion()).isEqualTo("^8.0.0");
        assertThat(plugin.createDSL()).isNotNull();
        assertThat(plugin.createDSL().getRenderer()).isEqualTo(renderer);
    }

    @Test
    void shouldRejectNullDialectName() {
        DialectRenderer renderer = mock(DialectRenderer.class);

        assertThatThrownBy(() -> new SqlDialectPlugin(null, "^8.0.0", () -> new DSL(renderer)))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Dialect name must not be null");
    }

    @Test
    void shouldRejectNullDialectVersion() {
        DialectRenderer renderer = mock(DialectRenderer.class);

        assertThatThrownBy(() -> new SqlDialectPlugin("mysql", null, () -> new DSL(renderer)))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Dialect version must not be null");
    }

    @Test
    void shouldRejectNullDslSupplier() {
        assertThatThrownBy(() -> new SqlDialectPlugin("mysql", "^8.0.0", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("DSL supplier must not be null");
    }

    @Test
    void shouldRejectBlankVersionRange() {
        DialectRenderer renderer = mock(DialectRenderer.class);

        assertThatThrownBy(() -> new SqlDialectPlugin("mysql", "", () -> new DSL(renderer)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dialect version must not be blank")
                .hasMessageContaining("mysql");

        assertThatThrownBy(() -> new SqlDialectPlugin("mysql", "   ", () -> new DSL(renderer)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dialect version must not be blank")
                .hasMessageContaining("mysql");
    }

    @Test
    void shouldAllowNonSemVerVersion() {
        DialectRenderer renderer = mock(DialectRenderer.class);

        // Non-SemVer versions like "2008" should be allowed for exact matching
        SqlDialectPlugin plugin = new SqlDialectPlugin("standardsql", "2008", () -> new DSL(renderer));

        assertThat(plugin.dialectName()).isEqualTo("standardsql");
        assertThat(plugin.dialectVersion()).isEqualTo("2008");
        assertThat(plugin.createDSL().getRenderer()).isEqualTo(renderer);
    }

    @Test
    void shouldAllowArbitraryVersionString() {
        DialectRenderer renderer = mock(DialectRenderer.class);

        // Arbitrary version strings should be allowed
        SqlDialectPlugin plugin1 = new SqlDialectPlugin("customdb", "2011", () -> new DSL(renderer));
        SqlDialectPlugin plugin2 = new SqlDialectPlugin("customdb", "v1", () -> new DSL(renderer));
        SqlDialectPlugin plugin3 = new SqlDialectPlugin("customdb", "latest", () -> new DSL(renderer));

        assertThat(plugin1.dialectVersion()).isEqualTo("2011");
        assertThat(plugin2.dialectVersion()).isEqualTo("v1");
        assertThat(plugin3.dialectVersion()).isEqualTo("latest");
    }

    @Test
    void shouldHaveValueSemantics() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        java.util.function.Supplier<DSL> dslFactory = () -> new DSL(renderer);

        SqlDialectPlugin plugin1 = new SqlDialectPlugin("mysql", "^8.0.0", dslFactory);
        SqlDialectPlugin plugin2 = new SqlDialectPlugin("mysql", "^8.0.0", dslFactory);

        // Value semantics: equals based on field values (same factory instance)
        assertThat(plugin1).isEqualTo(plugin2);
        assertThat(plugin1.hashCode()).isEqualTo(plugin2.hashCode());
    }

    @Test
    void shouldBeImmutable() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", () -> new DSL(renderer));

        // All fields are final by record definition
        assertThat(plugin.dialectName()).isEqualTo("mysql");
        assertThat(plugin.dialectVersion()).isEqualTo("^8.0.0");

        // Cannot mutate - compiler enforces this
        // No setters exist on records
    }

    @Test
    void shouldSupportMethodReferences() {
        // Record works well with functional programming
        DialectRenderer renderer = mock(DialectRenderer.class);

        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", () -> new DSL(renderer));

        DSL dsl1 = plugin.createDSL();
        DSL dsl2 = plugin.createDSL();

        assertThat(dsl1.getRenderer()).isEqualTo(renderer);
        assertThat(dsl2.getRenderer()).isEqualTo(renderer);
    }

    @Test
    void shouldHaveToString() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", () -> new DSL(renderer));

        String toString = plugin.toString();

        assertThat(toString).contains("mysql");
        assertThat(toString).contains("^8.0.0");
        assertThat(toString).contains("SqlDialectPlugin");
    }

    @Test
    void shouldCreateDSL() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", () -> new DSL(renderer));

        DSL dsl = plugin.createDSL();

        assertThat(dsl).isNotNull();
        assertThat(dsl.getRenderer()).isEqualTo(renderer);
    }

    @Test
    void shouldCreateNewDSLOnEachCall() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", () -> new DSL(renderer));

        DSL dsl1 = plugin.createDSL();
        DSL dsl2 = plugin.createDSL();

        assertThat(dsl1).isNotSameAs(dsl2);
    }
}
