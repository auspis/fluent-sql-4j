package lan.tlab.r4j.sql.dsl.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import org.junit.jupiter.api.Test;

class SqlDialectPluginTest {

    @Test
    void dialectNameIsNonNull() {
        SqlDialectPlugin plugin = new TestDialectPlugin();
        String dialectName = plugin.getDialectName();
        assertThat(dialectName).isNotNull();
    }

    @Test
    void dialectNameIsLowercase() {
        SqlDialectPlugin plugin = new TestDialectPlugin();
        String dialectName = plugin.getDialectName();
        assertThat(dialectName).isEqualTo(dialectName.toLowerCase());
    }

    @Test
    void versionIsNonNull() {
        SqlDialectPlugin plugin = new TestDialectPlugin();
        String version = plugin.getVersion();
        assertThat(version).isNotNull();
    }

    @Test
    void createRendererReturnsNonNull() {
        SqlDialectPlugin plugin = new TestDialectPlugin();
        SqlRenderer renderer = plugin.createRenderer();
        assertThat(renderer).isNotNull();
    }

    @Test
    void createRendererReturnsNewInstance() {
        SqlDialectPlugin plugin = new TestDialectPlugin();
        SqlRenderer renderer1 = plugin.createRenderer();
        SqlRenderer renderer2 = plugin.createRenderer();
        assertThat(renderer1).isNotSameAs(renderer2);
    }

    @Test
    void supportsCanonicalName() {
        SqlDialectPlugin plugin = new TestDialectPlugin();
        boolean result = plugin.supports("testdialect");
        assertThat(result).isTrue();
    }

    @Test
    void supportsCaseInsensitive() {
        SqlDialectPlugin plugin = new TestDialectPlugin();
        assertThat(plugin.supports("testdialect")).isTrue();
        assertThat(plugin.supports("TestDialect")).isTrue();
        assertThat(plugin.supports("TESTDIALECT")).isTrue();
    }

    @Test
    void supportsAliases() {
        SqlDialectPlugin plugin = new TestDialectPlugin();
        assertThat(plugin.supports("test")).isTrue();
        assertThat(plugin.supports("td")).isTrue();
    }

    @Test
    void doesNotSupportUnknownDialect() {
        SqlDialectPlugin plugin = new TestDialectPlugin();
        boolean result = plugin.supports("unknowndialect");
        assertThat(result).isFalse();
    }

    @Test
    void supportsNullDialectReturnsFalse() {
        SqlDialectPlugin plugin = new TestDialectPlugin();
        boolean result = plugin.supports(null);
        assertThat(result).isFalse();
    }

    @Test
    void supportedFeaturesIsNonNull() {
        SqlDialectPlugin plugin = new TestDialectPlugin();
        Set<String> features = plugin.getSupportedFeatures();
        assertThat(features).isNotNull();
    }

    @Test
    void supportedFeaturesContainsExpectedFeatures() {
        SqlDialectPlugin plugin = new TestDialectPlugin();
        Set<String> features = plugin.getSupportedFeatures();
        assertThat(features).contains("feature1", "feature2");
    }

    @Test
    void supportedFeaturesAreLowercase() {
        SqlDialectPlugin plugin = new TestDialectPlugin();
        Set<String> features = plugin.getSupportedFeatures();
        for (String feature : features) {
            assertThat(feature).isEqualTo(feature.toLowerCase());
        }
    }

    /**
     * Test implementation of SqlDialectPlugin for testing purposes.
     */
    private static class TestDialectPlugin implements SqlDialectPlugin {

        @Override
        public String getDialectName() {
            return "testdialect";
        }

        @Override
        public String getVersion() {
            return "1.0";
        }

        @Override
        public SqlRenderer createRenderer() {
            return SqlRenderer.builder().build();
        }

        @Override
        public boolean supports(String dialectName) {
            if (dialectName == null) {
                return false;
            }
            String lowerDialectName = dialectName.toLowerCase();
            return "testdialect".equals(lowerDialectName)
                    || "test".equals(lowerDialectName)
                    || "td".equals(lowerDialectName);
        }

        @Override
        public Set<String> getSupportedFeatures() {
            return Set.of("feature1", "feature2", "basic_select", "joins");
        }
    }
}
