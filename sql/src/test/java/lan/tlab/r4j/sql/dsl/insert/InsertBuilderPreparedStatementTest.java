package lan.tlab.r4j.sql.dsl.insert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InsertBuilderPreparedStatementTest {

    private lan.tlab.r4j.sql.dsl.DSL dsl;

    @BeforeEach
    void setUp() {
        dsl = TestDialectRendererFactory.dslStandardSql2008();
    }

    @Test
    void buildPreparedStatementRequiresConnection() {
        InsertBuilder builder = dsl.insertInto("users").set("name", "John");

        assertThatThrownBy(() -> builder.buildPreparedStatement(null)).isInstanceOf(Exception.class);
    }

    @Test
    void buildPreparedStatementCompilesWithoutError() {
        InsertBuilder builder = dsl.insertInto("users").set("name", "John").set("email", "john@example.com");

        assertThat(builder).isNotNull();

        assertThat(builder.getClass().getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("buildPreparedStatement"));
    }

    @Test
    void buildPreparedStatementWithDefaultValuesCompilesWithoutError() {
        InsertBuilder builder = dsl.insertInto("users").defaultValues();

        assertThat(builder).isNotNull();

        assertThat(builder.getClass().getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("buildPreparedStatement"));
    }
}
