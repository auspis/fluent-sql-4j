package lan.tlab.sqlbuilder.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SelectBuilderBasicTest {

    @Test
    void basicSelectAll() {
        String sql = DSL.selectAll().from("users").build();

        assertThat(sql).contains("SELECT * FROM");
        assertThat(sql).contains("users");
    }

    @Test
    void basicSelectWithColumns() {
        String sql = DSL.select("id", "name").from("users").build();

        assertThat(sql).contains("SELECT");
        assertThat(sql).contains("id");
        assertThat(sql).contains("name");
        assertThat(sql).contains("FROM");
        assertThat(sql).contains("users");
    }

    @Test
    void basicSelectWithWhere() {
        String sql = DSL.selectAll().from("users").where("id").eq(1).build();

        assertThat(sql).contains("SELECT * FROM");
        assertThat(sql).contains("users");
        assertThat(sql).contains("WHERE");
        assertThat(sql).contains("id");
        assertThat(sql).contains("= 1");
    }

    @Test
    void basicSelectWithOrderBy() {
        String sql = DSL.selectAll().from("users").orderBy("name").build();

        assertThat(sql).contains("SELECT * FROM");
        assertThat(sql).contains("users");
        assertThat(sql).contains("ORDER BY");
        assertThat(sql).contains("name");
        assertThat(sql).contains("ASC");
    }
}
