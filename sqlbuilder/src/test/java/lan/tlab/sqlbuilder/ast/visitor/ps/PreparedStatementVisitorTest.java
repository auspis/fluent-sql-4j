package lan.tlab.sqlbuilder.ast.visitor.ps;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy;
import lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy;
import lan.tlab.sqlbuilder.ast.clause.orderby.Sorting;
import lan.tlab.sqlbuilder.ast.clause.pagination.Pagination;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.Expression;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.bool.IsNotNull;
import lan.tlab.sqlbuilder.ast.expression.bool.IsNull;
import lan.tlab.sqlbuilder.ast.expression.bool.Like;
import lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr;
import lan.tlab.sqlbuilder.ast.expression.bool.logical.Not;
import lan.tlab.sqlbuilder.ast.expression.item.As;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertValues;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDate;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Concat;
import lan.tlab.sqlbuilder.ast.expression.scalar.convert.Cast;
import lan.tlab.sqlbuilder.ast.expression.set.UnionExpression;
import lan.tlab.sqlbuilder.ast.statement.DeleteStatement;
import lan.tlab.sqlbuilder.ast.statement.InsertStatement;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import org.junit.jupiter.api.Test;

class PreparedStatementVisitorTest {

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
        var insertValues = new InsertValues(values);
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
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(insertStatement, new AstContext());
        assertThat(result.sql()).isEqualTo("INSERT INTO \"User\" (\"id\", \"name\", \"email\") VALUES (?, ?, ?)");
        assertThat(result.parameters()).containsExactly(1, "John", "john@example.com");
    }

    @Test
    void testInsertStatementWithDefaultValues() {
        Table table = new Table("User");
        var defaultValues = new lan.tlab.sqlbuilder.ast.expression.item.InsertData.DefaultValues();
        InsertStatement insertStatement =
                InsertStatement.builder().table(table).data(defaultValues).build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(insertStatement, new AstContext());
        assertThat(result.sql()).isEqualTo("INSERT INTO \"User\" DEFAULT VALUES");
        assertThat(result.parameters()).isEmpty();
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
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\", \"name\" FROM \"User\" WHERE \"id\" = ?");
        assertThat(result.parameters()).containsExactly(1);
    }

    @Test
    void testSelectAllColumns() {
        SelectStatement selectStmt =
                SelectStatement.builder().from(From.of(new Table("users"))).build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
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
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
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
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
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
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
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
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
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
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
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
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" <> ?");
        assertThat(result.parameters()).containsExactly(99);
    }

    @Test
    void testSelectWhereAnd() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(AndOr.and(
                        Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10)),
                        Comparison.lt(ColumnReference.of("User", "id"), Literal.of(20)))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE (\"id\" > ?) AND (\"id\" < ?)");
        assertThat(result.parameters()).containsExactly(10, 20);
    }

    @Test
    void testSelectWhereOr() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(AndOr.or(
                        Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Alice")),
                        Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Bob")))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE (\"name\" = ?) OR (\"name\" = ?)");
        assertThat(result.parameters()).containsExactly("Alice", "Bob");
    }

    @Test
    void testSelectWhereAndOrNested() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(AndOr.or(
                        Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Alice")),
                        AndOr.and(
                                Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10)),
                                Comparison.lt(ColumnReference.of("User", "id"), Literal.of(20))))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT \"id\" FROM \"User\" WHERE (\"name\" = ?) OR ((\"id\" > ?) AND (\"id\" < ?))");
        assertThat(result.parameters()).containsExactly("Alice", 10, 20);
    }

    @Test
    void testSelectWhereNot() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(new Not(Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Alice")))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE NOT (\"name\" = ?)");
        assertThat(result.parameters()).containsExactly("Alice");
    }

    @Test
    void testSelectWhereIsNull() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "email"))))
                .from(From.of(new Table("User")))
                .where(Where.of(new IsNull(ColumnReference.of("User", "email"))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"email\" FROM \"User\" WHERE \"email\" IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectWhereIsNotNull() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "email"))))
                .from(From.of(new Table("User")))
                .where(Where.of(new IsNotNull(ColumnReference.of("User", "email"))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"email\" FROM \"User\" WHERE \"email\" IS NOT NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectWhereNotAnd() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(new Not(AndOr.and(
                        Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10)),
                        Comparison.lt(ColumnReference.of("User", "id"), Literal.of(20))))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE NOT ((\"id\" > ?) AND (\"id\" < ?))");
        assertThat(result.parameters()).containsExactly(10, 20);
    }

    @Test
    void testSelectWhereNotOr() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(new Not(AndOr.or(
                        Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Alice")),
                        Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Bob"))))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE NOT ((\"name\" = ?) OR (\"name\" = ?))");
        assertThat(result.parameters()).containsExactly("Alice", "Bob");
    }

    @Test
    void testSelectCount() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.count(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT COUNT(\"id\") FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectSum() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.sum(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT SUM(\"id\") FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectAvg() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.avg(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT AVG(\"id\") FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMin() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.min(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT MIN(\"id\") FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMax() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.max(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT MAX(\"id\") FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectCountGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.count(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT COUNT(\"id\") FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectSumGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.sum(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT SUM(\"id\") FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectAvgGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.avg(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT AVG(\"id\") FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMinGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.min(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT MIN(\"id\") FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMaxGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.max(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT MAX(\"id\") FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectScalarProjectionWithAlias() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("User", "id"), new As("userId")),
                        new ScalarExpressionProjection(ColumnReference.of("User", "name"), new As("userName"))))
                .from(From.of(new Table("User")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" AS \"userId\", \"name\" AS \"userName\" FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectScalarProjectionWithAliasAndWhere() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"), new As("userId"))))
                .from(From.of(new Table("User")))
                .where(Where.of(Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Alice"))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" AS \"userId\" FROM \"User\" WHERE \"name\" = ?");
        assertThat(result.parameters()).containsExactly("Alice");
    }

    @Test
    void testSelectAggregationProjectionWithAlias() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregationFunctionProjection(
                        AggregateCall.count(ColumnReference.of("User", "id")), new As("totalUsers"))))
                .from(From.of(new Table("User")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT COUNT(\"id\") AS \"totalUsers\" FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectAggregationProjectionWithAliasGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregationFunctionProjection(
                        AggregateCall.sum(ColumnReference.of("User", "id")), new As("sumId"))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT SUM(\"id\") AS \"sumId\" FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMultipleAggregationProjectionsWithAlias() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(
                                AggregateCall.avg(ColumnReference.of("User", "id")), new As("avgId")),
                        new AggregationFunctionProjection(
                                AggregateCall.max(ColumnReference.of("User", "id")), new As("maxId"))))
                .from(From.of(new Table("User")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT AVG(\"id\") AS \"avgId\", MAX(\"id\") AS \"maxId\" FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectOrderByIdAsc() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .orderBy(OrderBy.of(Sorting.asc(ColumnReference.of("User", "id"))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" ORDER BY \"id\" ASC");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectOrderByIdDesc() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .orderBy(OrderBy.of(Sorting.desc(ColumnReference.of("User", "id"))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" ORDER BY \"id\" DESC");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectOrderByIdDefault() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .orderBy(OrderBy.of(Sorting.by(ColumnReference.of("User", "id"))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
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
                .orderBy(OrderBy.of(
                        Sorting.asc(ColumnReference.of("User", "id")),
                        Sorting.desc(ColumnReference.of("User", "name"))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\", \"name\" FROM \"User\" ORDER BY \"id\" ASC, \"name\" DESC");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectOrderByWithWhere() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10))))
                .orderBy(OrderBy.of(Sorting.desc(ColumnReference.of("User", "id"))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" > ? ORDER BY \"id\" DESC");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void testSelectLimitOnly() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .pagination(Pagination.builder().perPage(10).build())
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" FETCH NEXT 10 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectLimitAndOffset() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .pagination(Pagination.builder().perPage(10).page(2).build())
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectWhereLimitOffset() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(Comparison.gt(ColumnReference.of("User", "id"), Literal.of(100))))
                .pagination(Pagination.builder().perPage(5).page(2).build())
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" > ? OFFSET 5 ROWS FETCH NEXT 5 ROWS ONLY");
        assertThat(result.parameters()).containsExactly(100);
    }

    @Test
    void testSelectOrderByLimitOffset() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .orderBy(OrderBy.of(Sorting.asc(ColumnReference.of("User", "id"))))
                .pagination(Pagination.builder().perPage(3).page(2).build())
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT \"id\" FROM \"User\" ORDER BY \"id\" ASC OFFSET 3 ROWS FETCH NEXT 3 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectGroupByLimitOffset() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "email"))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .pagination(Pagination.builder().perPage(2).page(3).build())
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT \"email\" FROM \"User\" GROUP BY \"email\" OFFSET 4 ROWS FETCH NEXT 2 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectInnerJoin() {
        var t1 = new Table("t1");
        var t2 = new Table("t2");
        var join = new lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin(
                t1,
                lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin.JoinType.INNER,
                t2,
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));
        SelectStatement stmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("t1", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("t2", "name"))))
                .from(From.of(join))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(stmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT \"id\", \"name\" FROM \"t1\" INNER JOIN \"t2\" ON \"t1\".\"id\" = \"t2\".\"t1_id\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectLeftJoinWithWhere() {
        var t1 = new Table("t1");
        var t2 = new Table("t2");
        var join = new lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin(
                t1,
                lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin.JoinType.LEFT,
                t2,
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));
        SelectStatement stmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("t1", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("t2", "name"))))
                .from(From.of(join))
                .where(Where.of(Comparison.gt(ColumnReference.of("t1", "id"), Literal.of(10))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(stmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT \"id\", \"name\" FROM \"t1\" LEFT JOIN \"t2\" ON \"t1\".\"id\" = \"t2\".\"t1_id\" WHERE \"id\" > ?");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void testSelectRightJoinWithOrderBy() {
        var t1 = new Table("t1");
        var t2 = new Table("t2");
        var join = new lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin(
                t1,
                lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin.JoinType.RIGHT,
                t2,
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));
        SelectStatement stmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("t1", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("t2", "name"))))
                .from(From.of(join))
                .orderBy(OrderBy.of(Sorting.asc(ColumnReference.of("t2", "name"))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(stmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT \"id\", \"name\" FROM \"t1\" RIGHT JOIN \"t2\" ON \"t1\".\"id\" = \"t2\".\"t1_id\" ORDER BY \"name\" ASC");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectFullJoinWithGroupBy() {
        var t1 = new Table("t1");
        var t2 = new Table("t2");
        var join = new lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin(
                t1,
                lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin.JoinType.FULL,
                t2,
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));
        SelectStatement stmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("t1", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("t2", "name"))))
                .from(From.of(join))
                .groupBy(GroupBy.of(ColumnReference.of("t1", "id")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(stmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT \"id\", \"name\" FROM \"t1\" FULL JOIN \"t2\" ON \"t1\".\"id\" = \"t2\".\"t1_id\" GROUP BY \"id\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectCrossJoinWithLimitOffset() {
        var t1 = new Table("t1");
        var t2 = new Table("t2");
        var join = new lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin(
                t1, lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin.JoinType.CROSS, t2, null);
        SelectStatement stmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("t1", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("t2", "name"))))
                .from(From.of(join))
                .pagination(Pagination.builder().perPage(5).page(1).build())
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(stmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT \"id\", \"name\" FROM \"t1\" CROSS JOIN \"t2\" FETCH NEXT 5 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMultipleJoins() {
        var t1 = new Table("t1");
        var t2 = new Table("t2");
        var t3 = new Table("t3");
        var join1 = new lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin(
                t1,
                lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin.JoinType.INNER,
                t2,
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));
        var join2 = new lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin(
                join1,
                lan.tlab.sqlbuilder.ast.clause.from.source.join.OnJoin.JoinType.LEFT,
                t3,
                Comparison.eq(ColumnReference.of("t2", "id"), ColumnReference.of("t3", "t2_id")));
        SelectStatement stmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("t1", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("t2", "name")),
                        new ScalarExpressionProjection(ColumnReference.of("t3", "value"))))
                .from(From.of(join2))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(stmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT \"id\", \"name\", \"value\" FROM \"t1\" INNER JOIN \"t2\" ON \"t1\".\"id\" = \"t2\".\"t1_id\" LEFT JOIN \"t3\" ON \"t2\".\"id\" = \"t3\".\"t2_id\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectCountGroupByHaving() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.count(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.sqlbuilder.ast.clause.conditional.having.Having.of(
                        Comparison.gt(AggregateCall.count(ColumnReference.of("User", "id")), Literal.of(1))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT COUNT(\"id\") FROM \"User\" GROUP BY \"email\" HAVING COUNT(\"id\") > ?");
        assertThat(result.parameters()).containsExactly(1);
    }

    @Test
    void testSelectSumGroupByHavingAnd() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.sum(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.sqlbuilder.ast.clause.conditional.having.Having.of(
                        lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr.and(
                                Comparison.gt(AggregateCall.sum(ColumnReference.of("User", "id")), Literal.of(10)),
                                Comparison.lt(AggregateCall.sum(ColumnReference.of("User", "id")), Literal.of(100)))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT SUM(\"id\") FROM \"User\" GROUP BY \"email\" HAVING (SUM(\"id\") > ?) AND (SUM(\"id\") < ?)");
        assertThat(result.parameters()).containsExactly(10, 100);
    }

    @Test
    void testSelectAvgGroupByHavingOr() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.avg(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.sqlbuilder.ast.clause.conditional.having.Having.of(
                        lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr.or(
                                Comparison.lt(AggregateCall.avg(ColumnReference.of("User", "id")), Literal.of(5)),
                                Comparison.gt(AggregateCall.avg(ColumnReference.of("User", "id")), Literal.of(50)))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT AVG(\"id\") FROM \"User\" GROUP BY \"email\" HAVING (AVG(\"id\") < ?) OR (AVG(\"id\") > ?)");
        assertThat(result.parameters()).containsExactly(5, 50);
    }

    @Test
    void testSelectGroupByHavingWithAlias() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregationFunctionProjection(
                        AggregateCall.count(ColumnReference.of("User", "id")), new As("total"))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.sqlbuilder.ast.clause.conditional.having.Having.of(
                        Comparison.gte(AggregateCall.count(ColumnReference.of("User", "id")), Literal.of(2))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT COUNT(\"id\") AS \"total\" FROM \"User\" GROUP BY \"email\" HAVING COUNT(\"id\") >= ?");
        assertThat(result.parameters()).containsExactly(2);
    }

    @Test
    void testSelectGroupByMultipleColumnsHaving() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.max(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email"), ColumnReference.of("User", "name")))
                .having(lan.tlab.sqlbuilder.ast.clause.conditional.having.Having.of(
                        Comparison.ne(AggregateCall.max(ColumnReference.of("User", "id")), Literal.of(0))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT MAX(\"id\") FROM \"User\" GROUP BY \"email\", \"name\" HAVING MAX(\"id\") <> ?");
        assertThat(result.parameters()).containsExactly(0);
    }

    @Test
    void testSelectGroupByHavingWithWhereOrderByLimit() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.min(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .where(Where.of(Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10))))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.sqlbuilder.ast.clause.conditional.having.Having.of(
                        Comparison.lt(AggregateCall.min(ColumnReference.of("User", "id")), Literal.of(100))))
                .orderBy(OrderBy.of(Sorting.desc(ColumnReference.of("User", "email"))))
                .pagination(Pagination.builder().perPage(5).page(1).build())
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT MIN(\"id\") FROM \"User\" WHERE \"id\" > ? GROUP BY \"email\" HAVING MIN(\"id\") < ? ORDER BY \"email\" DESC FETCH NEXT 5 ROWS ONLY");
        assertThat(result.parameters()).containsExactly(10, 100);
    }

    @Test
    void testSelectGroupByHavingIsNull() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.max(ColumnReference.of("User", "email")))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "name")))
                .having(lan.tlab.sqlbuilder.ast.clause.conditional.having.Having.of(
                        new IsNull(AggregateCall.max(ColumnReference.of("User", "email")))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT MAX(\"email\") FROM \"User\" GROUP BY \"name\" HAVING MAX(\"email\") IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectGroupByHavingIsNotNull() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.max(ColumnReference.of("User", "email")))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "name")))
                .having(lan.tlab.sqlbuilder.ast.clause.conditional.having.Having.of(
                        new IsNotNull(AggregateCall.max(ColumnReference.of("User", "email")))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT MAX(\"email\") FROM \"User\" GROUP BY \"name\" HAVING MAX(\"email\") IS NOT NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectGroupByHavingBetween() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.sum(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.sqlbuilder.ast.clause.conditional.having.Having.of(
                        new lan.tlab.sqlbuilder.ast.expression.bool.Between(
                                AggregateCall.sum(ColumnReference.of("User", "id")), Literal.of(10), Literal.of(100))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT SUM(\"id\") FROM \"User\" GROUP BY \"email\" HAVING SUM(\"id\") BETWEEN ? AND ?");
        assertThat(result.parameters()).containsExactly(10, 100);
    }

    @Test
    void testSelectGroupByHavingIn() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.count(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.sqlbuilder.ast.clause.conditional.having.Having.of(
                        new lan.tlab.sqlbuilder.ast.expression.bool.In(
                                AggregateCall.count(ColumnReference.of("User", "id")),
                                List.of(Literal.of(1), Literal.of(2), Literal.of(3)))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT COUNT(\"id\") FROM \"User\" GROUP BY \"email\" HAVING COUNT(\"id\") IN (?, ?, ?)");
        assertThat(result.parameters()).containsExactly(1, 2, 3);
    }

    @Test
    void testSelectGroupByHavingNotNested() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregationFunctionProjection(AggregateCall.count(ColumnReference.of("User", "id")))))
                .from(From.of(new Table("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.sqlbuilder.ast.clause.conditional.having.Having.of(
                        new Not(lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr.or(
                                Comparison.lt(AggregateCall.count(ColumnReference.of("User", "id")), Literal.of(5)),
                                Comparison.gt(AggregateCall.count(ColumnReference.of("User", "id")), Literal.of(50))))))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT COUNT(\"id\") FROM \"User\" GROUP BY \"email\" HAVING NOT ((COUNT(\"id\") < ?) OR (COUNT(\"id\") > ?))");
        assertThat(result.parameters()).containsExactly(5, 50);
    }

    @Test
    void testSelectPaginationFirstPage() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .pagination(Pagination.builder().perPage(10).page(1).build())
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" FETCH NEXT 10 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectPaginationLargeOffset() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .pagination(Pagination.builder().perPage(25).page(10).build())
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" OFFSET 225 ROWS FETCH NEXT 25 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectPaginationWithComplexQuery() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("User", "id")))) // Only project "id"
                .from(From.of(new Table("User")))
                .where(Where.of(AndOr.and(
                        Comparison.gt(ColumnReference.of("User", "id"), Literal.of(100)),
                        new Like(ColumnReference.of("User", "name"), "John%"))))
                .orderBy(OrderBy.of(
                        Sorting.asc(ColumnReference.of("User", "name")),
                        Sorting.desc(ColumnReference.of("User", "id"))))
                .pagination(Pagination.builder().perPage(15).page(3).build())
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT \"id\" FROM \"User\" WHERE (\"id\" > ?) AND (\"name\" LIKE ?) ORDER BY \"name\" ASC, \"id\" DESC OFFSET 30 ROWS FETCH NEXT 15 ROWS ONLY");
        assertThat(result.parameters()).containsExactly(100, "John%");
    }

    @Test
    void unionOfTwoSelectStatements() {
        SelectStatement select1 = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(Comparison.eq(ColumnReference.of("User", "id"), Literal.of(1))))
                .build();
        SelectStatement select2 = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(Comparison.eq(ColumnReference.of("User", "id"), Literal.of(2))))
                .build();
        UnionExpression union = UnionExpression.union(select1, select2);
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(union, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "((SELECT \"id\" FROM \"User\" WHERE \"id\" = ?) UNION (SELECT \"id\" FROM \"User\" WHERE \"id\" = ?))");
        assertThat(result.parameters()).containsExactly(1, 2);
    }

    @Test
    void selectFromSubquery() {
        var subquery = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10))))
                .build();

        var fromSubquery = lan.tlab.sqlbuilder.ast.clause.from.source.FromSubquery.of(subquery, "sub");

        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("sub", "id"))))
                .from(From.of(fromSubquery))
                .build();

        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());

        assertThat(result.sql())
                .isEqualTo("SELECT \"id\" FROM (SELECT \"id\" FROM \"User\" WHERE \"id\" > ?) AS \"sub\"");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void selectWhereNullBooleanExpression() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new Table("User")))
                .where(Where.of(new NullBooleanExpression()))
                .build();

        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());

        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void deleteStatement() {
        Table table = new Table("users");
        // Delete con where
        Comparison whereExpr = Comparison.eq(ColumnReference.of("", "id"), Literal.of(42));
        Where where = Where.builder().condition(whereExpr).build();
        DeleteStatement stmt =
                DeleteStatement.builder().table(table).where(where).build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto ps = visitor.visit(stmt, new AstContext());
        assertThat(ps.sql()).isEqualTo("DELETE FROM users WHERE \"id\" = ?");
        assertThat(ps.parameters()).containsExactly(42);
        // Delete senza where
        DeleteStatement stmtNoWhere = DeleteStatement.builder().table(table).build();
        PsDto psNoWhere = visitor.visit(stmtNoWhere, new AstContext());
        assertThat(psNoWhere.sql()).isEqualTo("DELETE FROM users");
        assertThat(psNoWhere.parameters()).isEmpty();
    }

    @Test
    void selectWithArithmeticAddition() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        ArithmeticExpression.addition(ColumnReference.of("Product", "price"), Literal.of(10)))))
                .from(From.of(new Table("Product")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT (\"price\" + ?) FROM \"Product\"");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void selectWithArithmeticSubtraction() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        ArithmeticExpression.subtraction(Literal.of(100), ColumnReference.of("Product", "discount")))))
                .from(From.of(new Table("Product")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT (? - \"discount\") FROM \"Product\"");
        assertThat(result.parameters()).containsExactly(100);
    }

    @Test
    void selectWithArithmeticMultiplication() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ArithmeticExpression.multiplication(
                        ColumnReference.of("Order", "quantity"), ColumnReference.of("Product", "price")))))
                .from(From.of(new Table("Order")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT (\"quantity\" * \"price\") FROM \"Order\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithArithmeticDivision() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        ArithmeticExpression.division(ColumnReference.of("Customer", "score"), Literal.of(2)))))
                .from(From.of(new Table("Customer")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT (\"score\" / ?) FROM \"Customer\"");
        assertThat(result.parameters()).containsExactly(2);
    }

    @Test
    void selectWithArithmeticModulo() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        ArithmeticExpression.modulo(ColumnReference.of("User", "id"), Literal.of(10)))))
                .from(From.of(new Table("User")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT (\"id\" % ?) FROM \"User\"");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void selectWithArithmeticNegation() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        ArithmeticExpression.negation(ColumnReference.of("Customer", "score")))))
                .from(From.of(new Table("Customer")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT (-\"score\") FROM \"Customer\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithArithmeticNegationLiteral() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ArithmeticExpression.negation(Literal.of(100)))))
                .from(From.of(new Table("Test")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT (-?) FROM \"Test\"");
        assertThat(result.parameters()).containsExactly(100);
    }

    @Test
    void selectWithCastLiteralToVarchar() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(Cast.of(Literal.of("hello"), "VARCHAR(50)"))))
                .from(From.of(new Table("Test")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CAST(? AS VARCHAR(50)) FROM \"Test\"");
        assertThat(result.parameters()).containsExactly("hello");
    }

    @Test
    void selectWithCastColumnToInt() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(Cast.of(ColumnReference.of("users", "age"), "INT"))))
                .from(From.of(new Table("Test")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CAST(\"age\" AS INT) FROM \"Test\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithCastNumberToDate() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(Cast.of(Literal.of(123), "DATE"))))
                .from(From.of(new Table("Test")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CAST(? AS DATE) FROM \"Test\"");
        assertThat(result.parameters()).containsExactly(123);
    }

    @Test
    void selectWithConcatTwoLiterals() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(Concat.concat(Literal.of("Hello"), Literal.of("World")))))
                .from(From.of(new Table("Test")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CONCAT(?, ?) FROM \"Test\"");
        assertThat(result.parameters()).containsExactly("Hello", "World");
    }

    @Test
    void selectWithConcatLiteralAndColumn() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        Concat.concat(Literal.of("Name: "), ColumnReference.of("users", "name")))))
                .from(From.of(new Table("Test")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CONCAT(?, \"name\") FROM \"Test\"");
        assertThat(result.parameters()).containsExactly("Name: ");
    }

    @Test
    void selectWithConcatWithSeparator() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        Concat.concatWithSeparator(" - ", Literal.of("First"), Literal.of("Second")))))
                .from(From.of(new Table("Test")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CONCAT_WS(?, ?, ?) FROM \"Test\"");
        assertThat(result.parameters()).containsExactly(" - ", "First", "Second");
    }

    @Test
    void selectWithCurrentDate() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(new CurrentDate())))
                .from(From.of(new Table("Test")))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CURRENT_DATE FROM \"Test\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithCurrentDateInComparison() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("orders", "created_date"))))
                .from(From.of(new Table("orders")))
                .where(Where.of(Comparison.eq(ColumnReference.of("orders", "created_date"), new CurrentDate())))
                .build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = visitor.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT \"created_date\" FROM \"orders\" WHERE \"created_date\" = CURRENT_DATE");
        assertThat(result.parameters()).isEmpty();
    }
}
