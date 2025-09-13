package lan.tlab.sqlbuilder.ast.visitor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.Expression;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.statement.InsertStatement;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import org.junit.jupiter.api.Test;

class PreparedSqlVisitorTest {

    static class User {
        public int id;
        public String name;
        public String email;

        public User(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }

    static InsertStatement buildInsertStatementFromUser(User user) {
        Table table = new Table("User");
        List<ColumnReference> columns = List.of(
                ColumnReference.of("User", "id"),
                ColumnReference.of("User", "name"),
                ColumnReference.of("User", "email"));
        List<Expression> values = List.of(Literal.of(user.id), Literal.of(user.name), Literal.of(user.email));
        var insertValues = new lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertValues(values);
        return InsertStatement.builder()
                .table(table)
                .columns(columns)
                .data(insertValues)
                .build();
    }

    @Test
    void testInsertStatement() {
        User user = new User(1, "John", "john@example.com");
        InsertStatement insertStatement = buildInsertStatementFromUser(user);
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(insertStatement);
        assertThat(result.sql()).isEqualTo("INSERT INTO \"User\" (\"id\", \"name\", \"email\") VALUES (?, ?, ?)");
        assertThat(result.parameters()).containsExactly(1, "John", "john@example.com");
    }

    @Test
    void testSelectStatement() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("User", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("User", "name"))))
                .from(From.of(new Table("User")))
                .where(Where.of(Comparison.eq(ColumnReference.of("User", "id"), Literal.of(1))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\", \"name\" FROM \"User\" WHERE \"id\" = ?");
        assertThat(result.parameters()).containsExactly(1);
    }

    @Test
    void testSelectAllColumns() {
        SelectStatement selectStmt =
                SelectStatement.builder().from(From.of(new Table("users"))).build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT * FROM \"users\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectWithTableAlias() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("u", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("u", "name"))))
                .from(From.fromTable("users", "u"))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\", \"name\" FROM \"users\" AS u");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectWhereGreaterThan() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" > ?");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void testSelectWhereLessThan() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(Comparison.lt(ColumnReference.of("User", "id"), Literal.of(5))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" < ?");
        assertThat(result.parameters()).containsExactly(5);
    }

    @Test
    void testSelectWhereGreaterThanOrEquals() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(Comparison.gte(ColumnReference.of("User", "id"), Literal.of(7))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" >= ?");
        assertThat(result.parameters()).containsExactly(7);
    }

    @Test
    void testSelectWhereLessThanOrEquals() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(Comparison.lte(ColumnReference.of("User", "id"), Literal.of(3))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" <= ?");
        assertThat(result.parameters()).containsExactly(3);
    }

    @Test
    void testSelectWhereNotEquals() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(Comparison.ne(ColumnReference.of("User", "id"), Literal.of(99))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" <> ?");
        assertThat(result.parameters()).containsExactly(99);
    }

    @Test
    void testSelectWhereAnd() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr.and(
                        Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10)),
                        Comparison.lt(ColumnReference.of("User", "id"), Literal.of(20)))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE (\"id\" > ?) AND (\"id\" < ?)");
        assertThat(result.parameters()).containsExactly(10, 20);
    }

    @Test
    void testSelectWhereOr() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr.or(
                        Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Alice")),
                        Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Bob")))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE (\"name\" = ?) OR (\"name\" = ?)");
        assertThat(result.parameters()).containsExactly("Alice", "Bob");
    }

    @Test
    void testSelectWhereAndOrNested() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr.or(
                        Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Alice")),
                        lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr.and(
                                Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10)),
                                Comparison.lt(ColumnReference.of("User", "id"), Literal.of(20))))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql())
                .isEqualTo("SELECT \"id\" FROM \"User\" WHERE (\"name\" = ?) OR ((\"id\" > ?) AND (\"id\" < ?))");
        assertThat(result.parameters()).containsExactly("Alice", 10, 20);
    }

    @Test
    void testSelectWhereNot() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(new lan.tlab.sqlbuilder.ast.expression.bool.logical.Not(
                        Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Alice")))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE NOT (\"name\" = ?)");
        assertThat(result.parameters()).containsExactly("Alice");
    }

    @Test
    void testSelectWhereIsNull() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "email"))))
                .from(From.of(new Table("User")))
                .where(Where.of(
                        new lan.tlab.sqlbuilder.ast.expression.bool.IsNull(ColumnReference.of("User", "email"))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"email\" FROM \"User\" WHERE \"email\" IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectWhereIsNotNull() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "email"))))
                .from(From.of(new Table("User")))
                .where(Where.of(
                        new lan.tlab.sqlbuilder.ast.expression.bool.IsNotNull(ColumnReference.of("User", "email"))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"email\" FROM \"User\" WHERE \"email\" IS NOT NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectWhereNotAnd() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(new lan.tlab.sqlbuilder.ast.expression.bool.logical.Not(
                        lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr.and(
                                Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10)),
                                Comparison.lt(ColumnReference.of("User", "id"), Literal.of(20))))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE NOT ((\"id\" > ?) AND (\"id\" < ?))");
        assertThat(result.parameters()).containsExactly(10, 20);
    }

    @Test
    void testSelectWhereNotOr() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(new lan.tlab.sqlbuilder.ast.expression.bool.logical.Not(
                        lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr.or(
                                Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Alice")),
                                Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Bob"))))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE NOT ((\"name\" = ?) OR (\"name\" = ?))");
        assertThat(result.parameters()).containsExactly("Alice", "Bob");
    }

    @Test
    void testSelectCount() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection(
                        lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall.count(
                                ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT COUNT(\"id\") FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectSum() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection(
                        lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall.sum(
                                ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT SUM(\"id\") FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectAvg() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection(
                        lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall.avg(
                                ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT AVG(\"id\") FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMin() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection(
                        lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall.min(
                                ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT MIN(\"id\") FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMax() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection(
                        lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall.max(
                                ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT MAX(\"id\") FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectCountGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection(
                        lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall.count(
                                ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT COUNT(\"id\") FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectSumGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection(
                        lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall.sum(
                                ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT SUM(\"id\") FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectAvgGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection(
                        lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall.avg(
                                ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT AVG(\"id\") FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMinGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection(
                        lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall.min(
                                ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT MIN(\"id\") FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMaxGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection(
                        lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall.max(
                                ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT MAX(\"id\") FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectScalarProjectionWithAlias() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(
                                ColumnReference.of("User", "id"),
                                new lan.tlab.sqlbuilder.ast.expression.item.As("userId")),
                        new ScalarExpressionProjection(
                                ColumnReference.of("User", "name"),
                                new lan.tlab.sqlbuilder.ast.expression.item.As("userName"))))
                .from(From.of(new Table("User")))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" AS \"userId\", \"name\" AS \"userName\" FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectScalarProjectionWithAliasAndWhere() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        ColumnReference.of("User", "id"), new lan.tlab.sqlbuilder.ast.expression.item.As("userId"))))
                .from(From.of(new Table("User")))
                .where(Where.of(Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Alice"))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" AS \"userId\" FROM \"User\" WHERE \"name\" = ?");
        assertThat(result.parameters()).containsExactly("Alice");
    }

    @Test
    void testSelectAggregationProjectionWithAlias() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection(
                        lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall.count(
                                ColumnReference.of("User", "id")),
                        new lan.tlab.sqlbuilder.ast.expression.item.As("totalUsers"))))
                .from(From.of(new Table("User")))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT COUNT(\"id\") AS \"totalUsers\" FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectAggregationProjectionWithAliasGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection(
                        lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall.sum(
                                ColumnReference.of("User", "id")),
                        new lan.tlab.sqlbuilder.ast.expression.item.As("sumId"))))
                .from(From.of(new Table("User")))
                .groupBy(lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT SUM(\"id\") AS \"sumId\" FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMultipleAggregationProjectionsWithAlias() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection(
                                lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall.avg(
                                        ColumnReference.of("User", "id")),
                                new lan.tlab.sqlbuilder.ast.expression.item.As("avgId")),
                        new lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection(
                                lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall.max(
                                        ColumnReference.of("User", "id")),
                                new lan.tlab.sqlbuilder.ast.expression.item.As("maxId"))))
                .from(From.of(new Table("User")))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT AVG(\"id\") AS \"avgId\", MAX(\"id\") AS \"maxId\" FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectOrderByIdAsc() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .orderBy(lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy.of(
                        lan.tlab.sqlbuilder.ast.clause.orderby.Sorting.asc(ColumnReference.of("User", "id"))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" ORDER BY \"id\" ASC");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectOrderByIdDesc() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .orderBy(lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy.of(
                        lan.tlab.sqlbuilder.ast.clause.orderby.Sorting.desc(ColumnReference.of("User", "id"))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" ORDER BY \"id\" DESC");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectOrderByIdDefault() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .orderBy(lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy.of(
                        lan.tlab.sqlbuilder.ast.clause.orderby.Sorting.by(ColumnReference.of("User", "id"))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" ORDER BY \"id\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectOrderByMultipleColumns() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("User", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("User", "name"))))
                .from(From.of(new Table("User")))
                .orderBy(lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy.of(
                        lan.tlab.sqlbuilder.ast.clause.orderby.Sorting.asc(ColumnReference.of("User", "id")),
                        lan.tlab.sqlbuilder.ast.clause.orderby.Sorting.desc(ColumnReference.of("User", "name"))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\", \"name\" FROM \"User\" ORDER BY \"id\" ASC, \"name\" DESC");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectOrderByWithWhere() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10))))
                .orderBy(lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy.of(
                        lan.tlab.sqlbuilder.ast.clause.orderby.Sorting.desc(ColumnReference.of("User", "id"))))
                .build();
        PreparedSqlVisitor visitor = new PreparedSqlVisitor();
        PreparedSqlResult result = visitor.visit(selectStmt);
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" > ? ORDER BY \"id\" DESC");
        assertThat(result.parameters()).containsExactly(10);
    }
}
