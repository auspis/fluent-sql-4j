package lan.tlab.r4j.sql.plugin.builtin.mysql.dsl.select.builders;

/**
 * This class is no longer needed as MysqlCoalesceBuilder.as() now returns MysqlSelectProjectionBuilder directly.
 * Kept for backwards compatibility during refactoring.
 *
 * @deprecated Use {@link MysqlCoalesceBuilder#as(String)} which now returns MysqlSelectProjectionBuilder directly
 */
@Deprecated(forRemoval = true)
public class MysqlCoalesceAliasBuilder {
    // This class will be removed in future versions
}
