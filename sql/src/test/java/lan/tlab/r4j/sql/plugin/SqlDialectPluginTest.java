package lan.tlab.r4j.sql.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import org.junit.jupiter.api.Test;

class SqlDialectPluginTest {

    @Test
    void shouldCreateValidPlugin() {
        SqlRenderer renderer = mock(SqlRenderer.class);
        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", () -> renderer);

        assertThat(plugin.dialectName()).isEqualTo("mysql");
        assertThat(plugin.dialectVersion()).isEqualTo("^8.0.0");
        assertThat(plugin.createRenderer()).isEqualTo(renderer);
    }

    @Test
    void shouldRejectNullDialectName() {
        SqlRenderer renderer = mock(SqlRenderer.class);

        assertThatThrownBy(() -> new SqlDialectPlugin(null, "^8.0.0", () -> renderer))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Dialect name must not be null");
    }

    @Test
    void shouldRejectNullDialectVersion() {
        SqlRenderer renderer = mock(SqlRenderer.class);

        assertThatThrownBy(() -> new SqlDialectPlugin("mysql", null, () -> renderer))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Dialect version must not be null");
    }

    @Test
    void shouldRejectNullRendererSupplier() {
        assertThatThrownBy(() -> new SqlDialectPlugin("mysql", "^8.0.0", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Renderer supplier must not be null");
    }

    @Test
    void shouldRejectInvalidVersionRange() {
        SqlRenderer renderer = mock(SqlRenderer.class);

        assertThatThrownBy(() -> new SqlDialectPlugin("mysql", "invalid-version", () -> renderer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid version range 'invalid-version'")
                .hasMessageContaining("mysql");
    }

    @Test
    void shouldHaveValueSemantics() {
        SqlRenderer renderer = mock(SqlRenderer.class);
        java.util.function.Supplier<SqlRenderer> factory = () -> renderer;

        SqlDialectPlugin plugin1 = new SqlDialectPlugin("mysql", "^8.0.0", factory);
        SqlDialectPlugin plugin2 = new SqlDialectPlugin("mysql", "^8.0.0", factory);

        // Value semantics: equals based on field values (same factory instance)
        assertThat(plugin1).isEqualTo(plugin2);
        assertThat(plugin1.hashCode()).isEqualTo(plugin2.hashCode());
    }

    @Test
    void shouldBeImmutable() {
        SqlRenderer renderer = mock(SqlRenderer.class);
        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", () -> renderer);

        // All fields are final by record definition
        assertThat(plugin.dialectName()).isEqualTo("mysql");
        assertThat(plugin.dialectVersion()).isEqualTo("^8.0.0");

        // Cannot mutate - compiler enforces this
        // No setters exist on records
    }

    @Test
    void shouldCreateNewRendererOnEachCall() {
        SqlRenderer renderer1 = mock(SqlRenderer.class);
        SqlRenderer renderer2 = mock(SqlRenderer.class);

        int[] callCount = {0};
        SqlDialectPlugin plugin =
                new SqlDialectPlugin("mysql", "^8.0.0", () -> callCount[0]++ == 0 ? renderer1 : renderer2);

        assertThat(plugin.createRenderer()).isEqualTo(renderer1);
        assertThat(plugin.createRenderer()).isEqualTo(renderer2);
    }

    @Test
    void shouldSupportMethodReferences() {
        // Record works well with functional programming
        SqlRenderer renderer = mock(SqlRenderer.class);

        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", () -> renderer);

        assertThat(plugin.createRenderer()).isEqualTo(renderer);
        assertThat(plugin.createRenderer()).isEqualTo(renderer);
    }

    @Test
    void shouldHaveToString() {
        SqlRenderer renderer = mock(SqlRenderer.class);
        SqlDialectPlugin plugin = new SqlDialectPlugin("mysql", "^8.0.0", () -> renderer);

        String toString = plugin.toString();

        assertThat(toString).contains("mysql");
        assertThat(toString).contains("^8.0.0");
        assertThat(toString).contains("SqlDialectPlugin");
    }
}
