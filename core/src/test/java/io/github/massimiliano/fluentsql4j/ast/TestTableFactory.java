package io.github.massimiliano.fluentsql4j.ast;

import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ColumnDefinition.ColumnDefinitionBuilder;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.TableDefinition;

public class TestTableFactory {

    public static TableDefinition customer() {
        return TableDefinition.builder()
                .name("Customer")
                .column(ColumnDefinitionBuilder.varchar("db_name").build())
                .column(ColumnDefinitionBuilder.varchar("db_surname").build())
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
