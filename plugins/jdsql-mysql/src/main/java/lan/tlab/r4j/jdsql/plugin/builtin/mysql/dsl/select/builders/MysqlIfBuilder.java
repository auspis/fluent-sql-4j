package lan.tlab.r4j.jdsql.plugin.builtin.mysql.dsl.select.builders;

import lan.tlab.r4j.jdsql.plugin.builtin.mysql.dsl.MysqlDSL;
import lan.tlab.r4j.jdsql.plugin.builtin.mysql.dsl.select.MysqlSelectProjectionBuilder;

/**
 * Entry point builder for MySQL IF function fluent API.
 * Initiates the condition specification.
 */
public class MysqlIfBuilder {

    private final MysqlSelectProjectionBuilder parent;
    private final MysqlDSL dsl;

    public MysqlIfBuilder(MysqlSelectProjectionBuilder parent, MysqlDSL dsl) {
        this.parent = parent;
        this.dsl = dsl;
    }

    /**
     * Starts the IF condition by specifying the column to test.
     *
     * @param column the column name to test
     * @return builder for specifying the comparison operator
     */
    public MysqlIfConditionBuilder when(String column) {
        return new MysqlIfConditionBuilder(parent, dsl, null, column);
    }

    /**
     * Starts the IF condition by specifying a column with table reference.
     *
     * @param table the table reference
     * @param column the column name to test
     * @return builder for specifying the comparison operator
     */
    public MysqlIfConditionBuilder when(String table, String column) {
        return new MysqlIfConditionBuilder(parent, dsl, table, column);
    }
}
