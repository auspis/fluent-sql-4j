package lan.tlab.r4j.jdsql.dsl.table;

import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.core.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import lan.tlab.r4j.jdsql.test.helper.SqlCaptureHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateTableBuilderTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void createUserTable() throws SQLException {
        new CreateTableBuilder(specFactory, "User")
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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
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
    void columnIntegerPrimaryKey() throws SQLException {
        new CreateTableBuilder(specFactory, "Test")
                .columnIntegerPrimaryKey("id")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        String sqlShortForm = sqlCaptureHelper.getSql();
        assertThat(sqlShortForm).contains("\"id\" INTEGER NOT NULL").contains("PRIMARY KEY (\"id\")");

        new CreateTableBuilder(specFactory, "Test")
                .column("id")
                .integer()
                .notNull()
                .primaryKey("id")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        String sqlLongForm = sqlCaptureHelper.getSql();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnStringPrimaryKey() throws SQLException {
        new CreateTableBuilder(specFactory, "Test")
                .columnStringPrimaryKey("code", 50)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        String sqlShortForm = sqlCaptureHelper.getSql();

        assertThat(sqlShortForm).contains("\"code\" VARCHAR(50) NOT NULL").contains("PRIMARY KEY (\"code\")");

        new CreateTableBuilder(specFactory, "Test")
                .column("code")
                .varchar(50)
                .notNull()
                .primaryKey("code")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        String sqlLongForm = sqlCaptureHelper.getSql();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnTimestampNotNull() throws SQLException {
        new CreateTableBuilder(specFactory, "Test")
                .columnTimestampNotNull("created_at")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        String sqlShortForm = sqlCaptureHelper.getSql();

        assertThat(sqlShortForm).contains("\"created_at\" TIMESTAMP NOT NULL");

        new CreateTableBuilder(specFactory, "Test")
                .column("created_at")
                .timestamp()
                .notNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        String sqlLongForm = sqlCaptureHelper.getSql();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnVarcharNotNull() throws SQLException {
        new CreateTableBuilder(specFactory, "Test")
                .columnVarcharNotNull("name", 100)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        String sqlShortForm = sqlCaptureHelper.getSql();

        assertThat(sqlShortForm).contains("\"name\" VARCHAR(100) NOT NULL");

        new CreateTableBuilder(specFactory, "Test")
                .column("name")
                .varchar(100)
                .notNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        String sqlLongForm = sqlCaptureHelper.getSql();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void columnDecimalNotNull() throws SQLException {
        new CreateTableBuilder(specFactory, "Test")
                .columnDecimalNotNull("price", 10, 2)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        String sqlShortForm = sqlCaptureHelper.getSql();

        assertThat(sqlShortForm).contains("\"price\" DECIMAL(10, 2) NOT NULL");

        new CreateTableBuilder(specFactory, "Test")
                .column("price")
                .decimal(10, 2)
                .notNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());
        String sqlLongForm = sqlCaptureHelper.getSql();

        assertThat(sqlShortForm).isEqualTo(sqlLongForm);
    }

    @Test
    void allConvenienceMethodsTogether() throws SQLException {
        new CreateTableBuilder(specFactory, "Product")
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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("\"id\" INTEGER NOT NULL")
                .contains("\"sku\" VARCHAR(20) NOT NULL")
                .contains("\"name\" VARCHAR(100) NOT NULL")
                .contains("\"price\" DECIMAL(10, 2) NOT NULL")
                .contains("\"created_at\" TIMESTAMP NOT NULL")
                .contains("PRIMARY KEY (\"id\", \"sku\")"); // Composite primary key
    }

    @Test
    void compositePrimaryKeyWithFluentApi() throws SQLException {
        new CreateTableBuilder(specFactory, "Orders")
                .column("customer_id")
                .integer()
                .notNull()
                .column("order_date")
                .date()
                .column("amount")
                .decimal(10, 2)
                .primaryKey("order_date", "customer_id") // Explicit order!
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("\"customer_id\" INTEGER NOT NULL")
                .contains("\"order_date\" DATE")
                .contains("\"amount\" DECIMAL(10, 2)")
                .contains("PRIMARY KEY (\"order_date\", \"customer_id\")"); // Ordine corretto
    }

    @Test
    void uniqueConstraint() throws SQLException {
        new CreateTableBuilder(specFactory, "Users")
                .column("id")
                .integer()
                .notNull()
                .column("email")
                .varchar(255)
                .unique()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("UNIQUE (\"email\")");
    }

    @Test
    void foreignKeyConstraint() throws SQLException {
        new CreateTableBuilder(specFactory, "Orders")
                .column("id")
                .integer()
                .notNull()
                .column("customer_id")
                .integer()
                .foreignKey("customer", "id")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("FOREIGN KEY (\"customer_id\") REFERENCES \"customer\" (\"id\")");
    }

    @Test
    void tableWithoutPrimaryKey() throws SQLException {
        new CreateTableBuilder(specFactory, "Log")
                .columnTimestampNotNull("timestamp")
                .columnVarcharNotNull("message", 500)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("\"timestamp\" TIMESTAMP NOT NULL")
                .contains("\"message\" VARCHAR(500) NOT NULL")
                .doesNotContain("PRIMARY KEY");
    }

    @Test
    void booleanColumn() throws SQLException {
        new CreateTableBuilder(specFactory, "Settings")
                .column("enabled")
                .bool()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"enabled\" BOOLEAN");
    }

    @Test
    void mixedFluentAndConvenienceApis() throws SQLException {
        new CreateTableBuilder(specFactory, "Mixed")
                .columnIntegerPrimaryKey("id")
                .column("description")
                .varchar(255)
                .notNull()
                .column("created_at")
                .timestamp()
                .notNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("\"id\" INTEGER NOT NULL")
                .contains("\"description\" VARCHAR(255) NOT NULL")
                .contains("\"created_at\" TIMESTAMP NOT NULL")
                .contains("PRIMARY KEY (\"id\")");
    }

    @Test
    void checkConstraint() throws SQLException {
        new CreateTableBuilder(specFactory, "People")
                .column("id")
                .integer()
                .notNull()
                .column("age")
                .integer()
                .column("name")
                .varchar(100)
                .check(Comparison.gt(ColumnReference.of("", "age"), Literal.of(18)))
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("CHECK (\"age\" > 18)");
    }

    @Test
    void defaultConstraint() throws SQLException {
        new CreateTableBuilder(specFactory, "Settings")
                .column("id")
                .integer()
                .notNull()
                .column("enabled")
                .bool()
                .defaultValue(Literal.of(true))
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("DEFAULT true");
    }

    @Test
    void singleIndex() throws SQLException {
        new CreateTableBuilder(specFactory, "Users")
                .column("id")
                .integer()
                .notNull()
                .column("email")
                .varchar(255)
                .index("idx_email", "email")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"email\" VARCHAR(255)").contains("INDEX \"idx_email\" (\"email\")");
    }

    @Test
    void compositeIndex() throws SQLException {
        new CreateTableBuilder(specFactory, "Orders")
                .column("customer_id")
                .integer()
                .column("order_date")
                .date()
                .index("idx_order_customer", "order_date", "customer_id")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("INDEX \"idx_order_customer\" (\"order_date\", \"customer_id\")");
    }

    @Test
    void columnWithoutExplicitTypeUsesDefault() throws SQLException {
        new CreateTableBuilder(specFactory, "Test")
                .column("default_column")
                .notNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        // ColumnDefinition has a default of VARCHAR(255)
        assertThatSql(sqlCaptureHelper).contains("\"default_column\" VARCHAR(255) NOT NULL");
    }

    @Test
    void primaryKeyWithExplicitOrderControl() throws SQLException {
        // Demonstrates explicit control of the order of columns in the primary key
        new CreateTableBuilder(specFactory, "OrderItems")
                .column("item_id")
                .integer()
                .notNull()
                .column("order_id")
                .integer()
                .notNull()
                .column("quantity")
                .integer()
                .primaryKey("order_id", "item_id") // Ordine esplicito: order_id prima di item_id
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("\"item_id\" INTEGER NOT NULL")
                .contains("\"order_id\" INTEGER NOT NULL")
                .contains("\"quantity\" INTEGER")
                .contains("PRIMARY KEY (\"order_id\", \"item_id\")"); // Correct order independent of declaration
    }
}
