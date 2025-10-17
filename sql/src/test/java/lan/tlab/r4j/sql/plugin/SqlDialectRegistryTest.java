package lan.tlab.r4j.sql.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class SqlDialectRegistryTest {

    @AfterEach
    void cleanup() {
        SqlDialectRegistry.clear();
    }

    @Test
    void shouldRegisterAndRetrievePlugin() {
        SqlDialectPlugin plugin = mock(SqlDialectPlugin.class);
        SqlRenderer renderer = mock(SqlRenderer.class);
        when(plugin.getDialectName()).thenReturn("testdialect");
        when(plugin.getDialectVersion()).thenReturn("^1.0.0");
        when(plugin.createRenderer()).thenReturn(renderer);

        SqlDialectRegistry.register(plugin);

        assertThat(SqlDialectRegistry.isSupported("testdialect")).isTrue();
        SqlRenderer result = SqlDialectRegistry.getRenderer("testdialect");
        assertThat(result).isEqualTo(renderer);
    }

    @Test
    void shouldMatchVersionWithinRange() {
        SqlRenderer renderer = mock(SqlRenderer.class);

        // Use a concrete implementation instead of mock to avoid Mockito issues
        SqlDialectPlugin plugin = new SqlDialectPlugin() {
            @Override
            public String getDialectName() {
                return "postgresql";
            }

            @Override
            public String getDialectVersion() {
                return "^14.0.0";
            }

            @Override
            public SqlRenderer createRenderer() {
                return renderer;
            }
        };

        SqlDialectRegistry.register(plugin);

        assertThat(SqlDialectRegistry.getRenderer("postgresql", "14.0.0")).isEqualTo(renderer);
        assertThat(SqlDialectRegistry.getRenderer("postgresql", "14.5.0")).isEqualTo(renderer);
    }

    @Test
    void shouldRejectVersionOutsideRange() {
        SqlDialectPlugin plugin = mock(SqlDialectPlugin.class);
        SqlRenderer renderer = mock(SqlRenderer.class);
        when(plugin.getDialectName()).thenReturn("mysql");
        when(plugin.getDialectVersion()).thenReturn("^8.0.0");
        when(plugin.createRenderer()).thenReturn(renderer);

        SqlDialectRegistry.register(plugin);

        assertThatThrownBy(() -> SqlDialectRegistry.getRenderer("mysql", "5.7.0"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No plugin found");
    }

    @Test
    void shouldThrowExceptionForNullDialect() {
        assertThatThrownBy(() -> SqlDialectRegistry.getRenderer(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dialect name must not be null");
    }

    @Test
    void shouldThrowExceptionForNullPlugin() {
        assertThatThrownBy(() -> SqlDialectRegistry.register(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Plugin must not be null");
    }

    @Test
    void shouldThrowExceptionForInvalidVersionRange() {
        SqlDialectPlugin plugin = mock(SqlDialectPlugin.class);
        when(plugin.getDialectName()).thenReturn("oracle");
        when(plugin.getDialectVersion()).thenReturn("invalid-range");

        assertThatThrownBy(() -> SqlDialectRegistry.register(plugin))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid version range 'invalid-range'")
                .hasMessageContaining("oracle");
    }

    @Test
    void shouldThrowExceptionForInvalidUserVersionFormat() {
        SqlDialectPlugin plugin = mock(SqlDialectPlugin.class);
        SqlRenderer renderer = mock(SqlRenderer.class);
        when(plugin.getDialectName()).thenReturn("postgres");
        when(plugin.getDialectVersion()).thenReturn("^13.0.0");
        when(plugin.createRenderer()).thenReturn(renderer);

        SqlDialectRegistry.register(plugin);

        assertThatThrownBy(() -> SqlDialectRegistry.getRenderer("postgres", "invalid-version"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid version format: 'invalid-version'");
    }
}
