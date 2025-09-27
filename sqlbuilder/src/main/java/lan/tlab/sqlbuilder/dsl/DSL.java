package lan.tlab.sqlbuilder.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DSL {

    public static TableBuilder createTable(String tableName) {
        return new TableBuilder(tableName);
    }

    public static class TableBuilder {
        private final String tableName;
        private final List<Column> columns = new ArrayList<>();

        public TableBuilder(String tableName) {
            this.tableName = tableName;
        }

        public ColumnBuilder column(String columnName) {
            return new ColumnBuilder(this, columnName);
        }

        void addColumn(Column column) {
            columns.add(column);
        }

        public String build() {
            String columnsDefinition = columns.stream().map(Column::toSql).collect(Collectors.joining(", "));
            return "CREATE TABLE " + tableName + " (" + columnsDefinition + ")";
        }
    }

    public static class ColumnBuilder {
        private final TableBuilder tableBuilder;
        private final String columnName;
        private String dataType;
        private final List<String> constraints = new ArrayList<>();

        public ColumnBuilder(TableBuilder tableBuilder, String columnName) {
            this.tableBuilder = tableBuilder;
            this.columnName = columnName;
        }

        public ColumnBuilder integer() {
            this.dataType = "INTEGER";
            return this;
        }

        public ColumnBuilder varchar(int length) {
            this.dataType = "VARCHAR(" + length + ")";
            return this;
        }

        public ColumnBuilder date() {
            this.dataType = "DATE";
            return this;
        }

        public ColumnBuilder decimal(int precision, int scale) {
            this.dataType = "DECIMAL(" + precision + ", " + scale + ")";
            return this;
        }

        public ColumnBuilder primaryKey() {
            constraints.add("PRIMARY KEY");
            return this;
        }

        public ColumnBuilder notNull() {
            constraints.add("NOT NULL");
            return this;
        }

        public ColumnBuilder column(String columnName) {
            finishColumn();
            return tableBuilder.column(columnName);
        }

        public String build() {
            finishColumn();
            return tableBuilder.build();
        }

        private void finishColumn() {
            tableBuilder.addColumn(new Column(columnName, dataType, constraints));
        }
    }

    private static class Column {
        private final String name;
        private final String dataType;
        private final List<String> constraints;

        public Column(String name, String dataType, List<String> constraints) {
            this.name = name;
            this.dataType = dataType;
            this.constraints = new ArrayList<>(constraints);
        }

        public String toSql() {
            StringBuilder sql = new StringBuilder();
            sql.append(name).append(" ").append(dataType);

            for (String constraint : constraints) {
                sql.append(" ").append(constraint);
            }

            return sql.toString();
        }
    }
}
