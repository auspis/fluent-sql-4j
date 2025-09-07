package lan.tlab.sqlbuilder.ast.expression.item.factory;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition.ColumnDefinitionBuilder;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;

public class TestTableFactory {

    public static TableDefinition customer() {
        return TableDefinition.builder()
                .name("Customer")
                .column(ColumnDefinitionBuilder.varchar("name").businessName("db_name").build())
                .column(ColumnDefinitionBuilder.varchar("surname").businessName("db_surname").build())
                .column(ColumnDefinitionBuilder.integer("score").build())
                .column(ColumnDefinitionBuilder.integer("rating").build())
                .column(ColumnDefinitionBuilder.date("birthdate").build())
                .build();
    }

    public static TableDefinition risk() {
        return TableDefinition.builder()
                .name("Risk")
                .column(ColumnDefinitionBuilder.varchar("description").build())
                .column(ColumnDefinitionBuilder.integer("value").build())
                .column(ColumnDefinitionBuilder.integer("level").build())
                .build();
    }

}
