package lan.tlab.r4j.sql.plugin;

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

    private static final String TEST_DIALECT_1 = "test-dialect-1";
    private static final String TEST_DIALECT_1_SHORT = "td1";
    private static final String TEST_DIALECT_2 = "test-dialect-2";
    private static final String V_1_0 = "1.0";
    private static final String V_2_0 = "2.0";
    private static final String FEATURE_1 = "feature-1";
    private static final String FEATURE_2 = "feature-2";
    private static final String FEATURE_3 = "feature-3";

    private SqlDialectPlugin plugin1;
    private SqlDialectPlugin plugin2;
    private SqlRenderer renderer1;
    private SqlRenderer renderer2;

    @BeforeEach
    void setUp() {
        // Create mock plugins
        plugin1 = mock(SqlDialectPlugin.class);
        renderer1 = mock(SqlRenderer.class);
        when(plugin1.getDialectName()).thenReturn(TEST_DIALECT_1);
        when(plugin1.getVersion()).thenReturn(V_1_0);
        when(plugin1.createRenderer()).thenReturn(renderer1);
        when(plugin1.supports(TEST_DIALECT_1)).thenReturn(true);
        when(plugin1.supports(TEST_DIALECT_1_SHORT)).thenReturn(true);
        when(plugin1.getSupportedFeatures()).thenReturn(Set.of(FEATURE_1, FEATURE_2));

        plugin2 = mock(SqlDialectPlugin.class);
        renderer2 = mock(SqlRenderer.class);
        when(plugin2.getDialectName()).thenReturn(TEST_DIALECT_2);
        when(plugin2.getVersion()).thenReturn(V_2_0);
        when(plugin2.createRenderer()).thenReturn(renderer2);
        when(plugin2.supports(TEST_DIALECT_2)).thenReturn(true);
        when(plugin2.getSupportedFeatures()).thenReturn(Set.of(FEATURE_3));

        SqlDialectRegistry.register(plugin1);
    }

    @Test
    void register() {
        assertThat(SqlDialectRegistry.isSupported(TEST_DIALECT_1)).isTrue();
        assertThat(SqlDialectRegistry.isSupported(TEST_DIALECT_2)).isFalse();
        assertThat(SqlDialectRegistry.getSupportedDialects()).contains(TEST_DIALECT_1);

        SqlRenderer renderer = SqlDialectRegistry.getRenderer(TEST_DIALECT_1);
        assertThat(renderer).isEqualTo(renderer1);
    }

    @Test
    void caseInsensitiveDialectNameLookup() {
        assertThat(SqlDialectRegistry.isSupported("TEST-DIALECT-1")).isTrue();
        assertThat(SqlDialectRegistry.isSupported("Test-Dialect-1")).isTrue();

        SqlRenderer renderer = SqlDialectRegistry.getRenderer(TEST_DIALECT_1);
        assertThat(SqlDialectRegistry.getRenderer("TEST-DIALECT-1")).isEqualTo(renderer);
        assertThat(SqlDialectRegistry.getRenderer("Test-Dialect-1")).isEqualTo(renderer);
    }

    @Test
    void dialectAliasSupport() {
        assertThat(SqlDialectRegistry.isSupported(TEST_DIALECT_1_SHORT)).isTrue();
        assertThat(SqlDialectRegistry.getRenderer(TEST_DIALECT_1_SHORT)).isEqualTo(renderer1);
    }

    @Test
    void registerMultiplePlugins() {
        SqlDialectRegistry.register(plugin2);

        assertThat(SqlDialectRegistry.getSupportedDialects()).contains(TEST_DIALECT_1, TEST_DIALECT_2);

        assertThat(SqlDialectRegistry.isSupported(TEST_DIALECT_1)).isTrue();
        assertThat(SqlDialectRegistry.isSupported(TEST_DIALECT_2)).isTrue();

        assertThat(SqlDialectRegistry.getRenderer(TEST_DIALECT_1)).isEqualTo(renderer1);
        assertThat(SqlDialectRegistry.getRenderer(TEST_DIALECT_2)).isEqualTo(renderer2);
    }

    @Test
    void duplicateRegistrationOverridesPrevious() {
        SqlDialectPlugin replacementPlugin = mock(SqlDialectPlugin.class);
        SqlRenderer replacementRenderer = mock(SqlRenderer.class);
        when(replacementPlugin.getDialectName()).thenReturn(TEST_DIALECT_1);
        when(replacementPlugin.getVersion()).thenReturn("1.1");
        when(replacementPlugin.createRenderer()).thenReturn(replacementRenderer);
        when(replacementPlugin.supports(TEST_DIALECT_1)).thenReturn(true);
        when(replacementPlugin.getSupportedFeatures()).thenReturn(Set.of(FEATURE_1));

        SqlDialectRegistry.register(replacementPlugin);

        // The replacement should be active
        SqlRenderer renderer = SqlDialectRegistry.getRenderer(TEST_DIALECT_1);
        assertThat(renderer).isEqualTo(replacementRenderer);
    }

    @Test
    void unsupportedDialectThrowsException() {
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
        assertThat(SqlDialectRegistry.isSupported("unknowndialect")).isFalse();
    }

    @Test
    void getSupportedDialectsReturnsImmutableSet() {
        Set<String> dialects = SqlDialectRegistry.getSupportedDialects();

        assertThatThrownBy(() -> dialects.add("newdialect")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void getSupportedDialectsReturnsCanonicalNamesOnly() {
        Set<String> dialects = SqlDialectRegistry.getSupportedDialects();

        // Should contain canonical name
        assertThat(dialects).contains(TEST_DIALECT_1);
        // Should not contain aliases
        assertThat(dialects).doesNotContain(TEST_DIALECT_1_SHORT);
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
                    when(plugin.getVersion()).thenReturn(V_1_0);
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
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    SqlRenderer renderer = SqlDialectRegistry.getRenderer(TEST_DIALECT_1);
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
                        when(plugin.getVersion()).thenReturn(V_1_0);
                        when(plugin.createRenderer()).thenReturn(renderer);
                        when(plugin.supports("concurrent" + index)).thenReturn(true);
                        when(plugin.getSupportedFeatures()).thenReturn(Set.of());

                        SqlDialectRegistry.register(plugin);
                        registrationCount.incrementAndGet();
                    } else if (index % 3 == 1) {
                        // Retrieval operation
                        SqlRenderer renderer = SqlDialectRegistry.getRenderer(TEST_DIALECT_1);
                        if (renderer != null) {
                            retrievalCount.incrementAndGet();
                        }
                    } else {
                        // Check operation
                        if (SqlDialectRegistry.isSupported(TEST_DIALECT_1)) {
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
