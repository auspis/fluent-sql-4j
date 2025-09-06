package lan.tlab.sqlbuilder.ast.expression.item.factory;

import java.sql.Connection;
import java.sql.SQLException;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;

public class TestTableFactory {

    public static TableDefinition customer() {
        return TableDefinition.builder()
                .name("Customer")
                .column(ColumnDefinition.string("name", "db_name"))
                .column(ColumnDefinition.string("surname", "db_surname"))
                .column(ColumnDefinition.integer("score"))
                .column(ColumnDefinition.integer("rating"))
                .column(ColumnDefinition.date("birthdate"))
                .build();
    }

    public static TableDefinition risk() {
        return TableDefinition.builder()
                .name("Risk")
                .column(ColumnDefinition.string("description"))
                .column(ColumnDefinition.integer("value"))
                .column(ColumnDefinition.integer("level"))
                .build();
    }

    public static class TableUtils {

        public static void createCustomerTable(Connection connection) throws SQLException {
            final var statement = connection.createStatement();

            statement.executeUpdate(
                    """
          CREATE TABLE Customer
          (id INTEGER AUTO_INCREMENT,
          db_name VARCHAR(255),
          db_surname VARCHAR(255),
          birthdate DATE,
          score INTEGER,
          rating INTEGER,
          PRIMARY KEY ( id ))
          """);
        }
    }
}
