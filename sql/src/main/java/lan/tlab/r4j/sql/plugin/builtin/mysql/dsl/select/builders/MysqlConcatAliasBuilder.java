package lan.tlab.r4j.sql.plugin.builtin.mysql.dsl.select.builders;

/**
 * This class is no longer needed as MysqlConcatBuilder.as() now returns MysqlSelectProjectionBuilder directly.
 * Kept for backwards compatibility during refactoring.
 *
 * @deprecated Use {@link MysqlConcatBuilder#as(String)} which now returns MysqlSelectProjectionBuilder directly
 */
@Deprecated(forRemoval = true)
public class MysqlConcatAliasBuilder {
    // This class will be removed in future versions
}
