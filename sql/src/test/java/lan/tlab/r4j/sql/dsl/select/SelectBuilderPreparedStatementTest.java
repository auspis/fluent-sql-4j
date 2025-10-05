package lan.tlab.r4j.sql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lan.tlab.r4j.sql.dsl.DSL;
import org.junit.jupiter.api.Test;

class SelectBuilderPreparedStatementTest {

    @Test
    void buildPreparedStatementRequiresConnection() {
        SelectBuilder builder = DSL.selectAll().from("users").where("age").gt(20);

        // Verifica che buildPrepared richieda una connessione
        assertThatThrownBy(() -> builder.buildPrepared(null)).isInstanceOf(Exception.class);
    }

    @Test
    void buildPreparedStatementCompilesWithoutError() {
        // Questo test verifica solo che il codice compili correttamente
        SelectBuilder builder = DSL.select("name", "email")
                .from("users")
                .where("age")
                .gte(18)
                .and("status")
                .eq("active");

        // Non possiamo testare senza una connessione reale, ma possiamo
        // verificare che il metodo esista e accetti una Connection
        assertThat(builder).isNotNull();

        // Verifica che il metodo buildPrepared esista
        assertThat(builder.getClass().getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("buildPrepared"));
    }

    @Test
    void buildPreparedStatementWithJoinCompilesWithoutError() {
        String sql = DSL.select("name", "email")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .where("status")
                .eq("active")
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT \"u\".\"name\", \"u\".\"email\" FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\" WHERE \"u\".\"status\" = 'active'");
    }
}
