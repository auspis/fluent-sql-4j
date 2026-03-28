package io.github.auspis.fluentsql4j.dsl.delete;

import static io.github.auspis.fluentsql4j.test.SqlAssert.assertThatSql;

import io.github.auspis.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.clause.LogicalCombinator;
import io.github.auspis.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.auspis.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NullPredicateDeleteConditionTest {

    private SqlCaptureHelper sqlCaptureHelper;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
    }

    @Test
    void nullPredicateAsSoleConditionProducesNoWhereClause() throws SQLException {
        new DeleteBuilder(specFactory, "users")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.AND)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("DELETE FROM \"users\"");
    }

    @Test
    void realConditionThenNullPredicateWithAndPreservesRealCondition() throws SQLException {
        new DeleteBuilder(specFactory, "users")
                .where()
                .column("status")
                .eq("inactive")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.AND)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("DELETE FROM \"users\" WHERE \"status\" = ?");
    }

    @Test
    void realConditionThenNullPredicateWithOrPreservesRealCondition() throws SQLException {
        new DeleteBuilder(specFactory, "users")
                .where()
                .column("status")
                .eq("inactive")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.OR)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("DELETE FROM \"users\" WHERE \"status\" = ?");
    }

    @Test
    void nullPredicateFirstThenRealConditionProducesOnlyRealCondition() throws SQLException {
        new DeleteBuilder(specFactory, "users")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.AND)
                .where()
                .column("status")
                .eq("inactive")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("DELETE FROM \"users\" WHERE \"status\" = ?");
    }

    @Test
    void twoNullPredicatesProduceNoWhereClause() throws SQLException {
        new DeleteBuilder(specFactory, "users")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.AND)
                .addWhereCondition(new NullPredicate(), LogicalCombinator.OR)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("DELETE FROM \"users\"");
    }

    @Test
    void twoRealConditionsThenNullPredicatePreservesBothRealConditions() throws SQLException {
        new DeleteBuilder(specFactory, "users")
                .where()
                .column("status")
                .eq("inactive")
                .and()
                .column("role")
                .eq("guest")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.AND)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("DELETE FROM \"users\" WHERE (\"status\" = ?) AND (\"role\" = ?)");
    }
}
