package io.github.auspis.fluentsql4j.dsl.select;

import static io.github.auspis.fluentsql4j.test.SqlAssert.assertThatSql;

import io.github.auspis.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.clause.LogicalCombinator;
import io.github.auspis.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.auspis.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NullPredicateWhereConditionTest {

    private SqlCaptureHelper sqlCaptureHelper;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
    }

    @Test
    void nullPredicateAsSoleConditionProducesNoWhereClause() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.AND)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"users\"");
    }

    @Test
    void realConditionThenNullPredicateWithAndPreservesRealCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("status")
                .eq("active")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.AND)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"users\" WHERE \"status\" = ?");
    }

    @Test
    void realConditionThenNullPredicateWithOrPreservesRealCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("status")
                .eq("active")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.OR)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"users\" WHERE \"status\" = ?");
    }

    @Test
    void nullPredicateFirstThenRealConditionProducesOnlyRealCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.AND)
                .where()
                .column("status")
                .eq("active")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"users\" WHERE \"status\" = ?");
    }

    @Test
    void twoNullPredicatesProduceNoWhereClause() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.AND)
                .addWhereCondition(new NullPredicate(), LogicalCombinator.OR)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"users\"");
    }

    @Test
    void twoRealConditionsThenNullPredicatePreservesBothRealConditions() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("status")
                .eq("active")
                .and()
                .column("role")
                .eq("admin")
                .addWhereCondition(new NullPredicate(), LogicalCombinator.AND)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"users\" WHERE (\"status\" = ?) AND (\"role\" = ?)");
    }
}
