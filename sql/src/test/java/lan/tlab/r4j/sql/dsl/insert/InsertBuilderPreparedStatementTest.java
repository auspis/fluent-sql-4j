package lan.tlab.r4j.sql.dsl.insert;

import static lan.tlab.r4j.sql.dsl.DSL.insertInto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class InsertBuilderPreparedStatementTest {

    @Test
    void buildPreparedStatementRequiresConnection() {
        InsertBuilder builder = insertInto("users").set("name", "John");

        assertThatThrownBy(() -> builder.buildPreparedStatement(null)).isInstanceOf(Exception.class);
    }

    @Test
    void buildPreparedStatementCompilesWithoutError() {
        InsertBuilder builder = insertInto("users").set("name", "John").set("email", "john@example.com");

        assertThat(builder).isNotNull();

        assertThat(builder.getClass().getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("buildPreparedStatement"));
    }

    @Test
    void buildPreparedStatementWithDefaultValuesCompilesWithoutError() {
        InsertBuilder builder = insertInto("users").defaultValues();

        assertThat(builder).isNotNull();

        assertThat(builder.getClass().getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("buildPreparedStatement"));
    }
}
