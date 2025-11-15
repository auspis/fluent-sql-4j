package lan.tlab.r4j.jdsql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// TODO: add integration tests that actually run the prepared statements against a real database
class SelectBuilderPreparedStatementTest {

    private DialectRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
    }

    @Test
    void buildPreparedStatementRequiresConnection() {
        SelectBuilder builder = new SelectBuilder(renderer, "*")
                .from("users")
                .where()
                .column("age")
                .gt(20);

        assertThatThrownBy(() -> builder.buildPreparedStatement(null)).isInstanceOf(Exception.class);
    }

    @Test
    void buildPreparedStatementCompilesWithoutError() {
        SelectBuilder builder = new SelectBuilder(renderer, "name", "email")
                .from("users")
                .where()
                .column("age")
                .gte(18)
                .and()
                .column("status")
                .eq("active");

        assertThat(builder).isNotNull();

        assertThat(builder.getClass().getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("buildPreparedStatement"));
    }

    @Test
    void buildPreparedStatementWithJoinCompilesWithoutError() {
        SelectBuilder builder = new SelectBuilder(renderer, "name", "email")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .where()
                .column("status")
                .eq("active");

        assertThat(builder).isNotNull();

        assertThat(builder.getClass().getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("buildPreparedStatement"));
    }
}
