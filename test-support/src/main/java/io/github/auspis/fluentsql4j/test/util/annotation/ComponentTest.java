package io.github.auspis.fluentsql4j.test.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;

/**
 * Marks a test class as a component test.
 * <p>
 * Component tests verify the interaction between multiple classes within a component
 * (e.g., DSL Builder + AST + Visitor + SQL Renderer) with real dependencies, but
 * isolate external infrastructure like databases using mocks or stubs.
 * <p>
 * These tests are faster than integration tests (no real database) but broader than
 * unit tests (test multiple collaborating classes). They run with unit tests by default.
 * <p>
 * Usage:
 * <pre>
 * &#64;ComponentTest
 * class SelectBuilderComponentTest {
 *     &#64;Test
 * void shouldGenerateCorrectSQL() {
 *         // test DSL API with mocked JDBC
 *     }
 * }
 * </pre>
 * <p>
 * To run component tests:
 * <pre>
 * ./mvnw test -Dgroups=component
 * </pre>
 * <p>
 * To run unit + component tests (fast feedback, no database):
 * <pre>
 * ./mvnw test -Dgroups="\!integration,\!e2e"
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag("component")
public @interface ComponentTest {}
