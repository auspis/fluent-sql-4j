package lan.tlab.r4j.sql.test.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;

/**
 * Marks a test class as an integration test.
 * <p>
 * Integration tests are tests that require external dependencies such as databases,
 * containers, or other services. They are executed by Maven Failsafe plugin during
 * the verify phase and can be run selectively using the 'integration' tag.
 * <p>
 * Usage:
 * <pre>
 * &#64;IntegrationTest
 * class MyIntegrationTest {
 *     &#64;Test
 *     void shouldIntegrateWithDatabase() {
 *         // test code
 *     }
 * }
 * </pre>
 * <p>
 * To run only integration tests:
 * <pre>
 * ./mvnw verify -Dgroups=integration
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag("integration")
public @interface IntegrationTest {}
