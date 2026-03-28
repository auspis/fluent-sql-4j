package io.github.auspis.fluentsql4j.dsl.update;

import static io.github.auspis.fluentsql4j.test.SqlAssert.assertThatSql;

import io.github.auspis.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.clause.LogicalCombinator;
import io.github.auspis.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.auspis.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NullPredicateUpdateConditionTest {

    private SqlCaptureHelper sqlCaptureHelper;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
    }

    @Test
    void nullPredicateAsSoleConditionProducesNoWhereClause() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("name", "John")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.AND)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("UPDATE \"users\" SET \"name\" = ?");
    }

    @Test
    void realConditionThenNullPredicateWithAndPreservesRealCondition() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("name", "John")
                .where()
                .column("id")
                .eq(1)
                .addWhereCondition(new NullPredicate(), LogicalCombinator.AND)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("UPDATE \"users\" SET \"name\" = ? WHERE \"id\" = ?");
    }

    @Test
    void realConditionThenNullPredicateWithOrPreservesRealCondition() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("name", "John")
                .where()
                .column("id")
                .eq(1)
                .addWhereCondition(new NullPredicate(), LogicalCombinator.OR)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("UPDATE \"users\" SET \"name\" = ? WHERE \"id\" = ?");
    }

    @Test
    void nullPredicateFirstThenRealConditionProducesOnlyRealCondition() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("name", "John")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.AND)
                .where()
                .column("id")
                .eq(1)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("UPDATE \"users\" SET \"name\" = ? WHERE \"id\" = ?");
    }

    @Test
    void twoNullPredicatesProduceNoWhereClause() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("name", "John")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.AND)
                .addWhereCondition(new NullPredicate(), LogicalCombinator.OR)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("UPDATE \"users\" SET \"name\" = ?");
    }

    @Test
    void twoRealConditionsThenNullPredicatePreservesBothRealConditions() throws SQLException {
        new UpdateBuilder(specFactory, "users")
                .set("name", "John")
                .where()
                .column("id")
                .eq(1)
                .and()
                .column("status")
                .eq("active")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.AND)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo("UPDATE \"users\" SET \"name\" = ? WHERE (\"id\" = ?) AND (\"status\" = ?)");
    }
}
