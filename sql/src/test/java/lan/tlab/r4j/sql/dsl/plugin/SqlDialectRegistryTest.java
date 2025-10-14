package lan.tlab.r4j.sql.dsl.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SqlDialectRegistryTest {

    private SqlDialectPlugin mockPlugin1;
    private SqlDialectPlugin mockPlugin2;
    private SqlRenderer mockRenderer1;
    private SqlRenderer mockRenderer2;

    @BeforeEach
    void setUp() {
        // Create mock plugins
        mockPlugin1 = mock(SqlDialectPlugin.class);
        mockRenderer1 = mock(SqlRenderer.class);
        when(mockPlugin1.getDialectName()).thenReturn("testdialect1");
        when(mockPlugin1.getVersion()).thenReturn("1.0");
        when(mockPlugin1.createRenderer()).thenReturn(mockRenderer1);
        when(mockPlugin1.supports("testdialect1")).thenReturn(true);
        when(mockPlugin1.supports("td1")).thenReturn(true);
        when(mockPlugin1.getSupportedFeatures()).thenReturn(Set.of("feature1", "feature2"));

        mockPlugin2 = mock(SqlDialectPlugin.class);
        mockRenderer2 = mock(SqlRenderer.class);
        when(mockPlugin2.getDialectName()).thenReturn("testdialect2");
        when(mockPlugin2.getVersion()).thenReturn("2.0");
        when(mockPlugin2.createRenderer()).thenReturn(mockRenderer2);
        when(mockPlugin2.supports("testdialect2")).thenReturn(true);
        when(mockPlugin2.getSupportedFeatures()).thenReturn(Set.of("feature3"));
    }

    @Test
    void registerAndRetrievePlugin() {
        SqlDialectRegistry.register(mockPlugin1);

        assertThat(SqlDialectRegistry.isSupported("testdialect1")).isTrue();
        assertThat(SqlDialectRegistry.getSupportedDialects()).contains("testdialect1");

        SqlRenderer renderer = SqlDialectRegistry.getRenderer("testdialect1");
        assertThat(renderer).isEqualTo(mockRenderer1);
    }

    @Test
    void caseInsensitiveDialectNameLookup() {
        SqlDialectRegistry.register(mockPlugin1);

        assertThat(SqlDialectRegistry.isSupported("TESTDIALECT1")).isTrue();
        assertThat(SqlDialectRegistry.isSupported("TestDialect1")).isTrue();
        assertThat(SqlDialectRegistry.isSupported("testdialect1")).isTrue();

        SqlRenderer renderer1 = SqlDialectRegistry.getRenderer("TESTDIALECT1");
        SqlRenderer renderer2 = SqlDialectRegistry.getRenderer("TestDialect1");
        SqlRenderer renderer3 = SqlDialectRegistry.getRenderer("testdialect1");

        assertThat(renderer1).isEqualTo(mockRenderer1);
        assertThat(renderer2).isEqualTo(mockRenderer1);
        assertThat(renderer3).isEqualTo(mockRenderer1);
    }

    @Test
    void dialectAliasSupport() {
        SqlDialectRegistry.register(mockPlugin1);

        // Test alias support via supports() method
        assertThat(SqlDialectRegistry.isSupported("td1")).isTrue();
        SqlRenderer renderer = SqlDialectRegistry.getRenderer("td1");
        assertThat(renderer).isEqualTo(mockRenderer1);
    }

    @Test
    void registerMultiplePlugins() {
        SqlDialectRegistry.register(mockPlugin1);
        SqlDialectRegistry.register(mockPlugin2);

        assertThat(SqlDialectRegistry.getSupportedDialects()).contains("testdialect1", "testdialect2");

        assertThat(SqlDialectRegistry.isSupported("testdialect1")).isTrue();
        assertThat(SqlDialectRegistry.isSupported("testdialect2")).isTrue();

        SqlRenderer renderer1 = SqlDialectRegistry.getRenderer("testdialect1");
        SqlRenderer renderer2 = SqlDialectRegistry.getRenderer("testdialect2");

        assertThat(renderer1).isEqualTo(mockRenderer1);
        assertThat(renderer2).isEqualTo(mockRenderer2);
    }

    @Test
    void duplicateRegistrationOverridesPrevious() {
        SqlDialectRegistry.register(mockPlugin1);

        // Create a second plugin with same dialect name
        SqlDialectPlugin replacementPlugin = mock(SqlDialectPlugin.class);
        SqlRenderer replacementRenderer = mock(SqlRenderer.class);
        when(replacementPlugin.getDialectName()).thenReturn("testdialect1");
        when(replacementPlugin.getVersion()).thenReturn("1.1");
        when(replacementPlugin.createRenderer()).thenReturn(replacementRenderer);
        when(replacementPlugin.supports("testdialect1")).thenReturn(true);
        when(replacementPlugin.getSupportedFeatures()).thenReturn(Set.of("feature1"));

        SqlDialectRegistry.register(replacementPlugin);

        // The replacement should be active
        SqlRenderer renderer = SqlDialectRegistry.getRenderer("testdialect1");
        assertThat(renderer).isEqualTo(replacementRenderer);
    }

    @Test
    void unsupportedDialectThrowsException() {
        SqlDialectRegistry.register(mockPlugin1);

        assertThatThrownBy(() -> SqlDialectRegistry.getRenderer("unknowndialect"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported SQL dialect: 'unknowndialect'")
                .hasMessageContaining("Supported dialects:");
    }

    @Test
    void nullDialectNameInGetRendererThrowsException() {
        assertThatThrownBy(() -> SqlDialectRegistry.getRenderer(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dialect name must not be null");
    }

    @Test
    void nullPluginInRegisterThrowsException() {
        assertThatThrownBy(() -> SqlDialectRegistry.register(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Plugin must not be null");
    }

    @Test
    void pluginWithNullDialectNameThrowsException() {
        SqlDialectPlugin badPlugin = mock(SqlDialectPlugin.class);
        when(badPlugin.getDialectName()).thenReturn(null);

        assertThatThrownBy(() -> SqlDialectRegistry.register(badPlugin))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Plugin dialect name must not be null");
    }

    @Test
    void isSupportedReturnsFalseForNull() {
        assertThat(SqlDialectRegistry.isSupported(null)).isFalse();
    }

    @Test
    void isSupportedReturnsFalseForUnknownDialect() {
        SqlDialectRegistry.register(mockPlugin1);
        assertThat(SqlDialectRegistry.isSupported("unknowndialect")).isFalse();
    }

    @Test
    void getSupportedDialectsReturnsImmutableSet() {
        SqlDialectRegistry.register(mockPlugin1);
        Set<String> dialects = SqlDialectRegistry.getSupportedDialects();

        assertThatThrownBy(() -> dialects.add("newdialect")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void getSupportedDialectsReturnsCanonicalNamesOnly() {
        SqlDialectRegistry.register(mockPlugin1);

        Set<String> dialects = SqlDialectRegistry.getSupportedDialects();

        // Should contain canonical name
        assertThat(dialects).contains("testdialect1");
        // Should not contain aliases
        assertThat(dialects).doesNotContain("td1");
    }

    @Test
    void concurrentRegistration() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    SqlDialectPlugin plugin = mock(SqlDialectPlugin.class);
                    SqlRenderer renderer = mock(SqlRenderer.class);
                    when(plugin.getDialectName()).thenReturn("dialect" + index);
                    when(plugin.getVersion()).thenReturn("1.0");
                    when(plugin.createRenderer()).thenReturn(renderer);
                    when(plugin.supports("dialect" + index)).thenReturn(true);
                    when(plugin.getSupportedFeatures()).thenReturn(Set.of("feature"));

                    SqlDialectRegistry.register(plugin);
                    successCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(successCount.get()).isEqualTo(threadCount);

        // Verify all plugins were registered
        for (int i = 0; i < threadCount; i++) {
            assertThat(SqlDialectRegistry.isSupported("dialect" + i)).isTrue();
        }
    }

    @Test
    void concurrentRetrieval() throws InterruptedException {
        SqlDialectRegistry.register(mockPlugin1);

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    SqlRenderer renderer = SqlDialectRegistry.getRenderer("testdialect1");
                    if (renderer != null) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(successCount.get()).isEqualTo(threadCount);
    }

    @Test
    void concurrentMixedOperations() throws InterruptedException {
        SqlDialectRegistry.register(mockPlugin1);

        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger registrationCount = new AtomicInteger(0);
        AtomicInteger retrievalCount = new AtomicInteger(0);
        AtomicInteger checkCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    if (index % 3 == 0) {
                        // Register operation
                        SqlDialectPlugin plugin = mock(SqlDialectPlugin.class);
                        SqlRenderer renderer = mock(SqlRenderer.class);
                        when(plugin.getDialectName()).thenReturn("concurrent" + index);
                        when(plugin.getVersion()).thenReturn("1.0");
                        when(plugin.createRenderer()).thenReturn(renderer);
                        when(plugin.supports("concurrent" + index)).thenReturn(true);
                        when(plugin.getSupportedFeatures()).thenReturn(Set.of());

                        SqlDialectRegistry.register(plugin);
                        registrationCount.incrementAndGet();
                    } else if (index % 3 == 1) {
                        // Retrieval operation
                        SqlRenderer renderer = SqlDialectRegistry.getRenderer("testdialect1");
                        if (renderer != null) {
                            retrievalCount.incrementAndGet();
                        }
                    } else {
                        // Check operation
                        if (SqlDialectRegistry.isSupported("testdialect1")) {
                            checkCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(registrationCount.get()).isGreaterThan(0);
        assertThat(retrievalCount.get()).isGreaterThan(0);
        assertThat(checkCount.get()).isGreaterThan(0);
    }
}
