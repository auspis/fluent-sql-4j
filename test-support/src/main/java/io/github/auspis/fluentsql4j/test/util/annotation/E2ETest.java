package io.github.auspis.fluentsql4j.test.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;

/**
 * Marks a test class as an end-to-end (E2E) test.
 * <p>
 * E2E tests are full system tests that verify the complete functionality
 * with real databases and external dependencies, typically using Testcontainers.
 * They are executed by Maven Failsafe plugin during the verify phase and can
 * be run selectively using the 'e2e' tag.
 * <p>
 * Usage:
 * <pre>
 * &#64;E2ETest
 * &#64;Testcontainers
 * class MyE2ETest {
 *     &#64;Test
 *     void shouldWorkEndToEnd() {
 *         // test code with real database
 *     }
 * }
 * </pre>
 * <p>
 * To run only E2E tests:
 * <pre>
 * ./mvnw verify -Dgroups=e2e
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag("e2e")
public @interface E2ETest {}
