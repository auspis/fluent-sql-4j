package lan.tlab.sqlbuilder.dsl.table;

import static lan.tlab.sqlbuilder.dsl.DSL.createTable;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TableBuilderTest {

    @Test
    void createUserTable() {
        String sql = createTable("User")
                .column("id")
                .integer()
                .notNull()
                .column("name")
                .varchar(100)
                .notNull()
                .column("email")
                .varchar(255)
                .column("birthdate")
                .date()
                .column("score")
                .decimal(10, 2)
                .primaryKey("id")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                    CREATE TABLE "User" (\
                    "id" INTEGER NOT NULL, \
                    "name" VARCHAR(100) NOT NULL, \
                    "email" VARCHAR(255), \
                    "birthdate" DATE, \
                    "score" DECIMAL(10, 2), \
                    PRIMARY KEY ("id")\
                    )""");
    }

    @Test
    void columnIntegerPrimaryKey() {
        String sqlShortForm = createTable("Test").columnIntegerPrimaryKey("id").build();
        assertThat(sqlShortForm).contains("\"id\" INTEGER NOT NULL").contains("PRIMARY KEY (\"id\")");

        String sqlLongForm = createTable("Test")
                .column("id")
                .integer()
                .notNull()
                .primaryKey("id")
                .build();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnStringPrimaryKey() {
        String sqlShortForm =
                createTable("Test").columnStringPrimaryKey("code", 50).build();

        assertThat(sqlShortForm).contains("\"code\" VARCHAR(50) NOT NULL").contains("PRIMARY KEY (\"code\")");

        String sqlLongForm = createTable("Test")
                .column("code")
                .varchar(50)
                .notNull()
                .primaryKey("code")
                .build();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnTimestampNotNull() {
        String sqlShortForm =
                createTable("Test").columnTimestampNotNull("created_at").build();

        assertThat(sqlShortForm).contains("\"created_at\" TIMESTAMP NOT NULL");

        String sqlLongForm =
                createTable("Test").column("created_at").timestamp().notNull().build();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnVarcharNotNull() {
        String sqlShortForm =
                createTable("Test").columnVarcharNotNull("name", 100).build();

        assertThat(sqlShortForm).contains("\"name\" VARCHAR(100) NOT NULL");

        String sqlLongForm =
                createTable("Test").column("name").varchar(100).notNull().build();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnDecimalNotNull() {
        String sqlShortForm =
                createTable("Test").columnDecimalNotNull("price", 10, 2).build();

        assertThat(sqlShortForm).contains("\"price\" DECIMAL(10, 2) NOT NULL");

        String sqlLongForm =
                createTable("Test").column("price").decimal(10, 2).notNull().build();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void allConvenienceMethodsTogether() {
        String sql = createTable("Product")
                .column("id")
                .integer()
                .notNull()
                .column("sku")
                .varchar(20)
                .notNull()
                .column("name")
                .varchar(100)
                .notNull()
                .column("price")
                .decimal(10, 2)
                .notNull()
                .column("created_at")
                .timestamp()
                .notNull()
                .primaryKey("id", "sku") // Explicit composite primary key
                .build();

        assertThat(sql)
                .contains("\"id\" INTEGER NOT NULL")
                .contains("\"sku\" VARCHAR(20) NOT NULL")
                .contains("\"name\" VARCHAR(100) NOT NULL")
                .contains("\"price\" DECIMAL(10, 2) NOT NULL")
                .contains("\"created_at\" TIMESTAMP NOT NULL")
                .contains("PRIMARY KEY (\"id\", \"sku\")"); // Composite primary key
    }

    @Test
    void compositePrimaryKeyWithFluentApi() {
        String sql = createTable("Orders")
                .column("customer_id")
                .integer()
                .notNull()
                .column("order_date")
                .date()
                .column("amount")
                .decimal(10, 2)
                .primaryKey("order_date", "customer_id") // Ordine esplicito!
                .build();

        assertThat(sql)
                .contains("\"customer_id\" INTEGER NOT NULL")
                .contains("\"order_date\" DATE")
                .contains("\"amount\" DECIMAL(10, 2)")
                .contains("PRIMARY KEY (\"order_date\", \"customer_id\")"); // Ordine corretto
    }

    @Test
    void tableWithoutPrimaryKey() {
        String sql = createTable("Log")
                .columnTimestampNotNull("timestamp")
                .columnVarcharNotNull("message", 500)
                .build();

        assertThat(sql)
                .contains("\"timestamp\" TIMESTAMP NOT NULL")
                .contains("\"message\" VARCHAR(500) NOT NULL")
                .doesNotContain("PRIMARY KEY");
    }

    @Test
    void booleanColumn() {
        String sql = createTable("Settings").column("enabled").bool().build();

        assertThat(sql).contains("\"enabled\" BOOLEAN");
    }

    @Test
    void mixedFluentAndConvenienceApis() {
        String sql = createTable("Mixed")
                .columnIntegerPrimaryKey("id")
                .column("description")
                .varchar(255)
                .notNull()
                .column("created_at")
                .timestamp()
                .notNull()
                .build();

        assertThat(sql)
                .contains("\"id\" INTEGER NOT NULL")
                .contains("\"description\" VARCHAR(255) NOT NULL")
                .contains("\"created_at\" TIMESTAMP NOT NULL")
                .contains("PRIMARY KEY (\"id\")");
    }

    @Test
    void columnWithoutExplicitTypeUsesDefault() {
        String sql = createTable("Test").column("default_column").notNull().build();

        // ColumnDefinition ha un default di VARCHAR(255)
        assertThat(sql).contains("\"default_column\" VARCHAR(255) NOT NULL");
    }

    @Test
    void primaryKeyWithExplicitOrderControl() {
        // Dimostra il controllo esplicito dell'ordine delle colonne nella primary key
        String sql = createTable("OrderItems")
                .column("item_id")
                .integer()
                .notNull()
                .column("order_id")
                .integer()
                .notNull()
                .column("quantity")
                .integer()
                .primaryKey("order_id", "item_id") // Ordine esplicito: order_id prima di item_id
                .build();

        assertThat(sql)
                .contains("\"item_id\" INTEGER NOT NULL")
                .contains("\"order_id\" INTEGER NOT NULL")
                .contains("\"quantity\" INTEGER")
                .contains(
                        "PRIMARY KEY (\"order_id\", \"item_id\")"); // Ordine corretto indipendente dalla dichiarazione
    }
}
