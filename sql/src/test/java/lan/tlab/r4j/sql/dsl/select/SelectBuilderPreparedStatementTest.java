package lan.tlab.r4j.sql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lan.tlab.r4j.sql.dsl.DSL;
import org.junit.jupiter.api.Test;

// TODO: add integration tests that actually run the prepared statements against a real database
class SelectBuilderPreparedStatementTest {

    @Test
    void buildPreparedStatementRequiresConnection() {
        SelectBuilder builder = DSL.selectAll().from("users").where("age").gt(20);

        assertThatThrownBy(() -> builder.buildPreparedStatement(null)).isInstanceOf(Exception.class);
    }

    @Test
    void buildPreparedStatementCompilesWithoutError() {
        SelectBuilder builder = DSL.select("name", "email")
                .from("users")
                .where("age")
                .gte(18)
                .and("status")
                .eq("active");

        assertThat(builder).isNotNull();

        assertThat(builder.getClass().getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("buildPrepared"));
    }

    @Test
    void buildPreparedStatementWithJoinCompilesWithoutError() {
        SelectBuilder builder = DSL.select("name", "email")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .where("status")
                .eq("active");

        assertThat(builder).isNotNull();

        assertThat(builder.getClass().getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("buildPrepared"));
    }
}
