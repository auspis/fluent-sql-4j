package lan.tlab.r4j.sql.dsl.table;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateTableBuilderTest {

    private DialectRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
    }

    @Test
    void createUserTable() {
        String sql = new CreateTableBuilder(renderer, "User")
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
        String sqlShortForm = new CreateTableBuilder(renderer, "Test")
                .columnIntegerPrimaryKey("id")
                .build();
        assertThat(sqlShortForm).contains("\"id\" INTEGER NOT NULL").contains("PRIMARY KEY (\"id\")");

        String sqlLongForm = new CreateTableBuilder(renderer, "Test")
                .column("id")
                .integer()
                .notNull()
                .primaryKey("id")
                .build();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnStringPrimaryKey() {
        String sqlShortForm = new CreateTableBuilder(renderer, "Test")
                .columnStringPrimaryKey("code", 50)
                .build();

        assertThat(sqlShortForm).contains("\"code\" VARCHAR(50) NOT NULL").contains("PRIMARY KEY (\"code\")");

        String sqlLongForm = new CreateTableBuilder(renderer, "Test")
                .column("code")
                .varchar(50)
                .notNull()
                .primaryKey("code")
                .build();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnTimestampNotNull() {
        String sqlShortForm = new CreateTableBuilder(renderer, "Test")
                .columnTimestampNotNull("created_at")
                .build();

        assertThat(sqlShortForm).contains("\"created_at\" TIMESTAMP NOT NULL");

        String sqlLongForm = new CreateTableBuilder(renderer, "Test")
                .column("created_at")
                .timestamp()
                .notNull()
                .build();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnVarcharNotNull() {
        String sqlShortForm = new CreateTableBuilder(renderer, "Test")
                .columnVarcharNotNull("name", 100)
                .build();

        assertThat(sqlShortForm).contains("\"name\" VARCHAR(100) NOT NULL");

        String sqlLongForm = new CreateTableBuilder(renderer, "Test")
                .column("name")
                .varchar(100)
                .notNull()
                .build();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnDecimalNotNull() {
        String sqlShortForm = new CreateTableBuilder(renderer, "Test")
                .columnDecimalNotNull("price", 10, 2)
                .build();

        assertThat(sqlShortForm).contains("\"price\" DECIMAL(10, 2) NOT NULL");

        String sqlLongForm = new CreateTableBuilder(renderer, "Test")
                .column("price")
                .decimal(10, 2)
                .notNull()
                .build();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void allConvenienceMethodsTogether() {
        String sql = new CreateTableBuilder(renderer, "Product")
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
        String sql = new CreateTableBuilder(renderer, "Orders")
                .column("customer_id")
                .integer()
                .notNull()
                .column("order_date")
                .date()
                .column("amount")
                .decimal(10, 2)
                .primaryKey("order_date", "customer_id") // Explicit order!
                .build();

        assertThat(sql)
                .contains("\"customer_id\" INTEGER NOT NULL")
                .contains("\"order_date\" DATE")
                .contains("\"amount\" DECIMAL(10, 2)")
                .contains("PRIMARY KEY (\"order_date\", \"customer_id\")"); // Ordine corretto
    }

    @Test
    void uniqueConstraint() {
        String sql = new CreateTableBuilder(renderer, "Users")
                .column("id")
                .integer()
                .notNull()
                .column("email")
                .varchar(255)
                .unique()
                .build();

        assertThat(sql).contains("UNIQUE (\"email\")");
    }

    @Test
    void foreignKeyConstraint() {
        String sql = new CreateTableBuilder(renderer, "Orders")
                .column("id")
                .integer()
                .notNull()
                .column("customer_id")
                .integer()
                .foreignKey("customer", "id")
                .build();

        assertThat(sql).contains("FOREIGN KEY (\"customer_id\") REFERENCES \"customer\" (\"id\")");
    }

    @Test
    void tableWithoutPrimaryKey() {
        String sql = new CreateTableBuilder(renderer, "Log")
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
        String sql = new CreateTableBuilder(renderer, "Settings")
                .column("enabled")
                .bool()
                .build();

        assertThat(sql).contains("\"enabled\" BOOLEAN");
    }

    @Test
    void mixedFluentAndConvenienceApis() {
        String sql = new CreateTableBuilder(renderer, "Mixed")
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
    void checkConstraint() {
        String sql = new CreateTableBuilder(renderer, "People")
                .column("id")
                .integer()
                .notNull()
                .column("age")
                .integer()
                .column("name")
                .varchar(100)
                .check(Comparison.gt(ColumnReference.of("", "age"), Literal.of(18)))
                .build();

        assertThat(sql).contains("CHECK (\"age\" > 18)");
    }

    @Test
    void defaultConstraint() {
        String sql = new CreateTableBuilder(renderer, "Settings")
                .column("id")
                .integer()
                .notNull()
                .column("enabled")
                .bool()
                .defaultValue(Literal.of(true))
                .build();

        assertThat(sql).contains("DEFAULT true");
    }

    @Test
    void singleIndex() {
        String sql = new CreateTableBuilder(renderer, "Users")
                .column("id")
                .integer()
                .notNull()
                .column("email")
                .varchar(255)
                .index("idx_email", "email")
                .build();

        assertThat(sql).contains("\"email\" VARCHAR(255)").contains("INDEX \"idx_email\" (\"email\")");
    }

    @Test
    void compositeIndex() {
        String sql = new CreateTableBuilder(renderer, "Orders")
                .column("customer_id")
                .integer()
                .column("order_date")
                .date()
                .index("idx_order_customer", "order_date", "customer_id")
                .build();

        assertThat(sql).contains("INDEX \"idx_order_customer\" (\"order_date\", \"customer_id\")");
    }

    @Test
    void columnWithoutExplicitTypeUsesDefault() {
        String sql = new CreateTableBuilder(renderer, "Test")
                .column("default_column")
                .notNull()
                .build();

        // ColumnDefinition has a default of VARCHAR(255)
        assertThat(sql).contains("\"default_column\" VARCHAR(255) NOT NULL");
    }

    @Test
    void primaryKeyWithExplicitOrderControl() {
        // Demonstrates explicit control of the order of columns in the primary key
        String sql = new CreateTableBuilder(renderer, "OrderItems")
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
                .contains("PRIMARY KEY (\"order_id\", \"item_id\")"); // Correct order independent of declaration
    }
}
