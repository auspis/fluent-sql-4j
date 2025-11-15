package lan.tlab.r4j.jdsql.ast.visitor.ps;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.Expression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ArithmeticExpression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Cast;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.NullScalarExpression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.sql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.CurrentDate;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.CurrentDateTime;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.DateArithmetic;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.ExtractDatePart;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.number.Mod;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.number.Power;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.number.Round;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.number.UnaryNumeric;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.CharLength;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.CharacterLength;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Concat;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Left;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Length;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Replace;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Substring;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Trim;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.UnaryString;
import lan.tlab.r4j.sql.ast.common.expression.set.UnionExpression;
import lan.tlab.r4j.sql.ast.common.identifier.Alias;
import lan.tlab.r4j.sql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.common.predicate.Comparison;
import lan.tlab.r4j.sql.ast.common.predicate.IsNotNull;
import lan.tlab.r4j.sql.ast.common.predicate.IsNull;
import lan.tlab.r4j.sql.ast.common.predicate.Like;
import lan.tlab.r4j.sql.ast.common.predicate.NullPredicate;
import lan.tlab.r4j.sql.ast.common.predicate.logical.AndOr;
import lan.tlab.r4j.sql.ast.common.predicate.logical.Not;
import lan.tlab.r4j.sql.ast.ddl.definition.ColumnDefinition.ColumnDefinitionBuilder;
import lan.tlab.r4j.sql.ast.ddl.definition.ConstraintDefinition;
import lan.tlab.r4j.sql.ast.ddl.definition.ReferencesItem;
import lan.tlab.r4j.sql.ast.ddl.definition.TableDefinition;
import lan.tlab.r4j.sql.ast.ddl.statement.CreateTableStatement;
import lan.tlab.r4j.sql.ast.dml.component.InsertData;
import lan.tlab.r4j.sql.ast.dml.component.InsertData.InsertValues;
import lan.tlab.r4j.sql.ast.dml.statement.DeleteStatement;
import lan.tlab.r4j.sql.ast.dml.statement.InsertStatement;
import lan.tlab.r4j.sql.ast.dql.clause.Fetch;
import lan.tlab.r4j.sql.ast.dql.clause.From;
import lan.tlab.r4j.sql.ast.dql.clause.GroupBy;
import lan.tlab.r4j.sql.ast.dql.clause.OrderBy;
import lan.tlab.r4j.sql.ast.dql.clause.Select;
import lan.tlab.r4j.sql.ast.dql.clause.Sorting;
import lan.tlab.r4j.sql.ast.dql.clause.Where;
import lan.tlab.r4j.sql.ast.dql.projection.AggregateCallProjection;
import lan.tlab.r4j.sql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.dql.source.FromSubquery;
import lan.tlab.r4j.sql.ast.dql.source.join.OnJoin;
import lan.tlab.r4j.sql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class PreparedStatementRendererTest {
    // TODO: remove User class
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
        TableIdentifier table = new TableIdentifier("User");
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
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(insertStatement, new AstContext());
        assertThat(result.sql()).isEqualTo("INSERT INTO \"User\" (\"id\", \"name\", \"email\") VALUES (?, ?, ?)");
        assertThat(result.parameters()).containsExactly(1, "John", "john@example.com");
    }

    @Test
    void testInsertStatementWithDefaultValues() {
        TableIdentifier table = new TableIdentifier("User");
        var defaultValues = new InsertData.DefaultValues();
        InsertStatement insertStatement =
                InsertStatement.builder().table(table).data(defaultValues).build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(insertStatement, new AstContext());
        assertThat(result.sql()).isEqualTo("INSERT INTO \"User\" DEFAULT VALUES");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectStatement() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("User", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("User", "name"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(Comparison.eq(ColumnReference.of("User", "id"), Literal.of(1))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\", \"name\" FROM \"User\" WHERE \"id\" = ?");
        assertThat(result.parameters()).containsExactly(1);
    }

    @Test
    void testSelectAllColumns() {
        SelectStatement selectStmt = SelectStatement.builder()
                .from(From.of(new TableIdentifier("users")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
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
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\", \"name\" FROM \"users\" AS u");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectWhereGreaterThan() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" > ?");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void testSelectWhereLessThan() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(Comparison.lt(ColumnReference.of("User", "id"), Literal.of(5))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" < ?");
        assertThat(result.parameters()).containsExactly(5);
    }

    @Test
    void testSelectWhereGreaterThanOrEquals() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(Comparison.gte(ColumnReference.of("User", "id"), Literal.of(7))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" >= ?");
        assertThat(result.parameters()).containsExactly(7);
    }

    @Test
    void testSelectWhereLessThanOrEquals() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(Comparison.lte(ColumnReference.of("User", "id"), Literal.of(3))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" <= ?");
        assertThat(result.parameters()).containsExactly(3);
    }

    @Test
    void testSelectWhereNotEquals() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(Comparison.ne(ColumnReference.of("User", "id"), Literal.of(99))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" <> ?");
        assertThat(result.parameters()).containsExactly(99);
    }

    @Test
    void testSelectWhereAnd() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(AndOr.and(
                        Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10)),
                        Comparison.lt(ColumnReference.of("User", "id"), Literal.of(20)))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE (\"id\" > ?) AND (\"id\" < ?)");
        assertThat(result.parameters()).containsExactly(10, 20);
    }

    @Test
    void testSelectWhereOr() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(AndOr.or(
                        Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Alice")),
                        Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Bob")))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE (\"name\" = ?) OR (\"name\" = ?)");
        assertThat(result.parameters()).containsExactly("Alice", "Bob");
    }

    @Test
    void testSelectWhereAndOrNested() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(AndOr.or(
                        Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Alice")),
                        AndOr.and(
                                Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10)),
                                Comparison.lt(ColumnReference.of("User", "id"), Literal.of(20))))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT \"id\" FROM \"User\" WHERE (\"name\" = ?) OR ((\"id\" > ?) AND (\"id\" < ?))");
        assertThat(result.parameters()).containsExactly("Alice", 10, 20);
    }

    @Test
    void testSelectWhereNot() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(new Not(Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Alice")))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE NOT (\"name\" = ?)");
        assertThat(result.parameters()).containsExactly("Alice");
    }

    @Test
    void testSelectWhereIsNull() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "email"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(new IsNull(ColumnReference.of("User", "email"))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"email\" FROM \"User\" WHERE \"email\" IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectWhereIsNotNull() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "email"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(new IsNotNull(ColumnReference.of("User", "email"))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"email\" FROM \"User\" WHERE \"email\" IS NOT NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectWhereNotAnd() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(new Not(AndOr.and(
                        Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10)),
                        Comparison.lt(ColumnReference.of("User", "id"), Literal.of(20))))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE NOT ((\"id\" > ?) AND (\"id\" < ?))");
        assertThat(result.parameters()).containsExactly(10, 20);
    }

    @Test
    void testSelectWhereNotOr() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(new Not(AndOr.or(
                        Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Alice")),
                        Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Bob"))))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE NOT ((\"name\" = ?) OR (\"name\" = ?))");
        assertThat(result.parameters()).containsExactly("Alice", "Bob");
    }

    @Test
    void testSelectCount() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.count(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT COUNT(\"id\") FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectSum() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.sum(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT SUM(\"id\") FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectAvg() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.avg(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT AVG(\"id\") FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMin() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.min(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT MIN(\"id\") FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMax() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.max(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT MAX(\"id\") FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectCountGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.count(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT COUNT(\"id\") FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectSumGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.sum(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT SUM(\"id\") FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectAvgGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.avg(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT AVG(\"id\") FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMinGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.min(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT MIN(\"id\") FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMaxGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.max(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT MAX(\"id\") FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectScalarProjectionWithAlias() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("User", "id"), new Alias("userId")),
                        new ScalarExpressionProjection(ColumnReference.of("User", "name"), new Alias("userName"))))
                .from(From.of(new TableIdentifier("User")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" AS \"userId\", \"name\" AS \"userName\" FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectScalarProjectionWithAliasAndWhere() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("User", "id"), new Alias("userId"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(Comparison.eq(ColumnReference.of("User", "name"), Literal.of("Alice"))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" AS \"userId\" FROM \"User\" WHERE \"name\" = ?");
        assertThat(result.parameters()).containsExactly("Alice");
    }

    @Test
    void testSelectAggregationProjectionWithAlias() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(
                        AggregateCall.count(ColumnReference.of("User", "id")), new Alias("totalUsers"))))
                .from(From.of(new TableIdentifier("User")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT COUNT(\"id\") AS \"totalUsers\" FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectAggregationProjectionWithAliasGroupBy() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(
                        AggregateCall.sum(ColumnReference.of("User", "id")), new Alias("sumId"))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT SUM(\"id\") AS \"sumId\" FROM \"User\" GROUP BY \"email\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMultipleAggregationProjectionsWithAlias() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new AggregateCallProjection(
                                AggregateCall.avg(ColumnReference.of("User", "id")), new Alias("avgId")),
                        new AggregateCallProjection(
                                AggregateCall.max(ColumnReference.of("User", "id")), new Alias("maxId"))))
                .from(From.of(new TableIdentifier("User")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT AVG(\"id\") AS \"avgId\", MAX(\"id\") AS \"maxId\" FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectOrderByIdAsc() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .orderBy(OrderBy.of(Sorting.asc(ColumnReference.of("User", "id"))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" ORDER BY \"id\" ASC");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectOrderByIdDesc() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .orderBy(OrderBy.of(Sorting.desc(ColumnReference.of("User", "id"))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" ORDER BY \"id\" DESC");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectOrderByIdDefault() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .orderBy(OrderBy.of(Sorting.by(ColumnReference.of("User", "id"))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" ORDER BY \"id\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectOrderByMultipleColumns() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("User", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("User", "name"))))
                .from(From.of(new TableIdentifier("User")))
                .orderBy(OrderBy.of(
                        Sorting.asc(ColumnReference.of("User", "id")),
                        Sorting.desc(ColumnReference.of("User", "name"))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\", \"name\" FROM \"User\" ORDER BY \"id\" ASC, \"name\" DESC");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectOrderByWithWhere() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10))))
                .orderBy(OrderBy.of(Sorting.desc(ColumnReference.of("User", "id"))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" > ? ORDER BY \"id\" DESC");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void testSelectLimitOnly() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .fetch(new Fetch(0, 10))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" FETCH NEXT 10 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectLimitAndOffset() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .fetch(new Fetch(10, 10))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectWhereLimitOffset() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(Comparison.gt(ColumnReference.of("User", "id"), Literal.of(100))))
                .fetch(new Fetch(5, 5))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT \"id\" FROM \"User\" WHERE \"id\" > ? OFFSET 5 ROWS FETCH NEXT 5 ROWS ONLY");
        assertThat(result.parameters()).containsExactly(100);
    }

    @Test
    void testSelectOrderByLimitOffset() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .orderBy(OrderBy.of(Sorting.asc(ColumnReference.of("User", "id"))))
                .fetch(new Fetch(3, 3))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT \"id\" FROM \"User\" ORDER BY \"id\" ASC OFFSET 3 ROWS FETCH NEXT 3 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectGroupByLimitOffset() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "email"))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .fetch(new Fetch(4, 2))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT \"email\" FROM \"User\" GROUP BY \"email\" OFFSET 4 ROWS FETCH NEXT 2 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectInnerJoin() {
        var t1 = new TableIdentifier("t1");
        var t2 = new TableIdentifier("t2");
        var join = new OnJoin(
                t1,
                OnJoin.JoinType.INNER,
                t2,
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));
        SelectStatement stmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("t1", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("t2", "name"))))
                .from(From.of(join))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(stmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT \"id\", \"name\" FROM \"t1\" INNER JOIN \"t2\" ON \"t1\".\"id\" = \"t2\".\"t1_id\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectLeftJoinWithWhere() {
        var t1 = new TableIdentifier("t1");
        var t2 = new TableIdentifier("t2");
        var join = new OnJoin(
                t1,
                OnJoin.JoinType.LEFT,
                t2,
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));
        SelectStatement stmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("t1", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("t2", "name"))))
                .from(From.of(join))
                .where(Where.of(Comparison.gt(ColumnReference.of("t1", "id"), Literal.of(10))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(stmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT \"id\", \"name\" FROM \"t1\" LEFT JOIN \"t2\" ON \"t1\".\"id\" = \"t2\".\"t1_id\" WHERE \"id\" > ?");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void testSelectRightJoinWithOrderBy() {
        var t1 = new TableIdentifier("t1");
        var t2 = new TableIdentifier("t2");
        var join = new OnJoin(
                t1,
                OnJoin.JoinType.RIGHT,
                t2,
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));
        SelectStatement stmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("t1", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("t2", "name"))))
                .from(From.of(join))
                .orderBy(OrderBy.of(Sorting.asc(ColumnReference.of("t2", "name"))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(stmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT \"id\", \"name\" FROM \"t1\" RIGHT JOIN \"t2\" ON \"t1\".\"id\" = \"t2\".\"t1_id\" ORDER BY \"name\" ASC");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectFullJoinWithGroupBy() {
        var t1 = new TableIdentifier("t1");
        var t2 = new TableIdentifier("t2");
        var join = new OnJoin(
                t1,
                OnJoin.JoinType.FULL,
                t2,
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));
        SelectStatement stmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("t1", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("t2", "name"))))
                .from(From.of(join))
                .groupBy(GroupBy.of(ColumnReference.of("t1", "id")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(stmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT \"id\", \"name\" FROM \"t1\" FULL JOIN \"t2\" ON \"t1\".\"id\" = \"t2\".\"t1_id\" GROUP BY \"id\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectCrossJoinWithLimitOffset() {
        var t1 = new TableIdentifier("t1");
        var t2 = new TableIdentifier("t2");
        var join = new OnJoin(t1, OnJoin.JoinType.CROSS, t2, null);
        SelectStatement stmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("t1", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("t2", "name"))))
                .from(From.of(join))
                .fetch(new Fetch(0, 5))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(stmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT \"id\", \"name\" FROM \"t1\" CROSS JOIN \"t2\" FETCH NEXT 5 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectMultipleJoins() {
        var t1 = new TableIdentifier("t1");
        var t2 = new TableIdentifier("t2");
        var t3 = new TableIdentifier("t3");
        var join1 = new OnJoin(
                t1,
                OnJoin.JoinType.INNER,
                t2,
                Comparison.eq(ColumnReference.of("t1", "id"), ColumnReference.of("t2", "t1_id")));
        var join2 = new OnJoin(
                join1,
                OnJoin.JoinType.LEFT,
                t3,
                Comparison.eq(ColumnReference.of("t2", "id"), ColumnReference.of("t3", "t2_id")));
        SelectStatement stmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("t1", "id")),
                        new ScalarExpressionProjection(ColumnReference.of("t2", "name")),
                        new ScalarExpressionProjection(ColumnReference.of("t3", "value"))))
                .from(From.of(join2))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(stmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT \"id\", \"name\", \"value\" FROM \"t1\" INNER JOIN \"t2\" ON \"t1\".\"id\" = \"t2\".\"t1_id\" LEFT JOIN \"t3\" ON \"t2\".\"id\" = \"t3\".\"t2_id\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectCountGroupByHaving() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.count(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.r4j.sql.ast.dql.clause.Having.of(
                        Comparison.gt(AggregateCall.count(ColumnReference.of("User", "id")), Literal.of(1))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT COUNT(\"id\") FROM \"User\" GROUP BY \"email\" HAVING COUNT(\"id\") > ?");
        assertThat(result.parameters()).containsExactly(1);
    }

    @Test
    void testSelectSumGroupByHavingAnd() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.sum(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.r4j.sql.ast.dql.clause.Having.of(
                        lan.tlab.r4j.sql.ast.common.predicate.logical.AndOr.and(
                                Comparison.gt(AggregateCall.sum(ColumnReference.of("User", "id")), Literal.of(10)),
                                Comparison.lt(AggregateCall.sum(ColumnReference.of("User", "id")), Literal.of(100)))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT SUM(\"id\") FROM \"User\" GROUP BY \"email\" HAVING (SUM(\"id\") > ?) AND (SUM(\"id\") < ?)");
        assertThat(result.parameters()).containsExactly(10, 100);
    }

    @Test
    void testSelectAvgGroupByHavingOr() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.avg(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.r4j.sql.ast.dql.clause.Having.of(
                        lan.tlab.r4j.sql.ast.common.predicate.logical.AndOr.or(
                                Comparison.lt(AggregateCall.avg(ColumnReference.of("User", "id")), Literal.of(5)),
                                Comparison.gt(AggregateCall.avg(ColumnReference.of("User", "id")), Literal.of(50)))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT AVG(\"id\") FROM \"User\" GROUP BY \"email\" HAVING (AVG(\"id\") < ?) OR (AVG(\"id\") > ?)");
        assertThat(result.parameters()).containsExactly(5, 50);
    }

    @Test
    void testSelectGroupByHavingWithAlias() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(
                        AggregateCall.count(ColumnReference.of("User", "id")), new Alias("total"))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.r4j.sql.ast.dql.clause.Having.of(
                        Comparison.gte(AggregateCall.count(ColumnReference.of("User", "id")), Literal.of(2))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT COUNT(\"id\") AS \"total\" FROM \"User\" GROUP BY \"email\" HAVING COUNT(\"id\") >= ?");
        assertThat(result.parameters()).containsExactly(2);
    }

    @Test
    void testSelectGroupByMultipleColumnsHaving() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.max(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email"), ColumnReference.of("User", "name")))
                .having(lan.tlab.r4j.sql.ast.dql.clause.Having.of(
                        Comparison.ne(AggregateCall.max(ColumnReference.of("User", "id")), Literal.of(0))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT MAX(\"id\") FROM \"User\" GROUP BY \"email\", \"name\" HAVING MAX(\"id\") <> ?");
        assertThat(result.parameters()).containsExactly(0);
    }

    @Test
    void testSelectGroupByHavingWithWhereOrderByLimit() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.min(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10))))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.r4j.sql.ast.dql.clause.Having.of(
                        Comparison.lt(AggregateCall.min(ColumnReference.of("User", "id")), Literal.of(100))))
                .orderBy(OrderBy.of(Sorting.desc(ColumnReference.of("User", "email"))))
                .fetch(new Fetch(0, 5))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT MIN(\"id\") FROM \"User\" WHERE \"id\" > ? GROUP BY \"email\" HAVING MIN(\"id\") < ? ORDER BY \"email\" DESC FETCH NEXT 5 ROWS ONLY");
        assertThat(result.parameters()).containsExactly(10, 100);
    }

    @Test
    void testSelectGroupByHavingIsNull() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.max(ColumnReference.of("User", "email")))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "name")))
                .having(lan.tlab.r4j.sql.ast.dql.clause.Having.of(
                        new IsNull(AggregateCall.max(ColumnReference.of("User", "email")))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT MAX(\"email\") FROM \"User\" GROUP BY \"name\" HAVING MAX(\"email\") IS NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectGroupByHavingIsNotNull() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.max(ColumnReference.of("User", "email")))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "name")))
                .having(lan.tlab.r4j.sql.ast.dql.clause.Having.of(
                        new IsNotNull(AggregateCall.max(ColumnReference.of("User", "email")))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT MAX(\"email\") FROM \"User\" GROUP BY \"name\" HAVING MAX(\"email\") IS NOT NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectGroupByHavingBetween() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.sum(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.r4j.sql.ast.dql.clause.Having.of(new lan.tlab.r4j.sql.ast.common.predicate.Between(
                        AggregateCall.sum(ColumnReference.of("User", "id")), Literal.of(10), Literal.of(100))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT SUM(\"id\") FROM \"User\" GROUP BY \"email\" HAVING SUM(\"id\") BETWEEN ? AND ?");
        assertThat(result.parameters()).containsExactly(10, 100);
    }

    @Test
    void testSelectGroupByHavingIn() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.count(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.r4j.sql.ast.dql.clause.Having.of(new lan.tlab.r4j.sql.ast.common.predicate.In(
                        AggregateCall.count(ColumnReference.of("User", "id")),
                        List.of(Literal.of(1), Literal.of(2), Literal.of(3)))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT COUNT(\"id\") FROM \"User\" GROUP BY \"email\" HAVING COUNT(\"id\") IN (?, ?, ?)");
        assertThat(result.parameters()).containsExactly(1, 2, 3);
    }

    @Test
    void testSelectGroupByHavingNotNested() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.count(ColumnReference.of("User", "id")))))
                .from(From.of(new TableIdentifier("User")))
                .groupBy(GroupBy.of(ColumnReference.of("User", "email")))
                .having(lan.tlab.r4j.sql.ast.dql.clause.Having.of(
                        new Not(lan.tlab.r4j.sql.ast.common.predicate.logical.AndOr.or(
                                Comparison.lt(AggregateCall.count(ColumnReference.of("User", "id")), Literal.of(5)),
                                Comparison.gt(AggregateCall.count(ColumnReference.of("User", "id")), Literal.of(50))))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT COUNT(\"id\") FROM \"User\" GROUP BY \"email\" HAVING NOT ((COUNT(\"id\") < ?) OR (COUNT(\"id\") > ?))");
        assertThat(result.parameters()).containsExactly(5, 50);
    }

    @Test
    void testSelectfetchFirstPage() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .fetch(new Fetch(0, 10))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" FETCH NEXT 10 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectfetchLargeOffset() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .fetch(new Fetch(225, 25))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\" OFFSET 225 ROWS FETCH NEXT 25 ROWS ONLY");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void testSelectfetchWithComplexQuery() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("User", "id")))) // Only project "id"
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(AndOr.and(
                        Comparison.gt(ColumnReference.of("User", "id"), Literal.of(100)),
                        new Like(ColumnReference.of("User", "name"), "John%"))))
                .orderBy(OrderBy.of(
                        Sorting.asc(ColumnReference.of("User", "name")),
                        Sorting.desc(ColumnReference.of("User", "id"))))
                .fetch(new Fetch(30, 15))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "SELECT \"id\" FROM \"User\" WHERE (\"id\" > ?) AND (\"name\" LIKE ?) ORDER BY \"name\" ASC, \"id\" DESC OFFSET 30 ROWS FETCH NEXT 15 ROWS ONLY");
        assertThat(result.parameters()).containsExactly(100, "John%");
    }

    @Test
    void unionOfTwoSelectStatements() {
        SelectStatement select1 = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(Comparison.eq(ColumnReference.of("User", "id"), Literal.of(1))))
                .build();
        SelectStatement select2 = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(Comparison.eq(ColumnReference.of("User", "id"), Literal.of(2))))
                .build();
        UnionExpression union = UnionExpression.union(select1, select2);
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(union, new AstContext());
        assertThat(result.sql())
                .isEqualTo(
                        "((SELECT \"id\" FROM \"User\" WHERE \"id\" = ?) UNION (SELECT \"id\" FROM \"User\" WHERE \"id\" = ?))");
        assertThat(result.parameters()).containsExactly(1, 2);
    }

    @Test
    void selectFromSubquery() {
        var subquery = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(Comparison.gt(ColumnReference.of("User", "id"), Literal.of(10))))
                .build();

        var fromSubquery = FromSubquery.of(subquery, "sub");

        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("sub", "id"))))
                .from(From.of(fromSubquery))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql())
                .isEqualTo("SELECT \"id\" FROM (SELECT \"id\" FROM \"User\" WHERE \"id\" > ?) AS \"sub\"");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void selectWhereNullPredicate() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("User", "id"))))
                .from(From.of(new TableIdentifier("User")))
                .where(Where.of(new NullPredicate()))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"User\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void deleteStatement() {
        TableIdentifier table = new TableIdentifier("users");
        // Delete con where
        Comparison whereExpr = Comparison.eq(ColumnReference.of("", "id"), Literal.of(42));
        Where where = Where.of(whereExpr);
        DeleteStatement stmt =
                DeleteStatement.builder().table(table).where(where).build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto ps = renderer.visit(stmt, new AstContext());
        assertThat(ps.sql()).isEqualTo("DELETE FROM users WHERE \"id\" = ?");
        assertThat(ps.parameters()).containsExactly(42);
        // Delete senza where
        DeleteStatement stmtNoWhere = DeleteStatement.builder().table(table).build();
        PsDto psNoWhere = renderer.visit(stmtNoWhere, new AstContext());
        assertThat(psNoWhere.sql()).isEqualTo("DELETE FROM users");
        assertThat(psNoWhere.parameters()).isEmpty();
    }

    @Test
    void selectWithArithmeticAddition() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        ArithmeticExpression.addition(ColumnReference.of("Product", "price"), Literal.of(10)))))
                .from(From.of(new TableIdentifier("Product")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT (\"price\" + ?) FROM \"Product\"");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void selectWithArithmeticSubtraction() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        ArithmeticExpression.subtraction(Literal.of(100), ColumnReference.of("Product", "discount")))))
                .from(From.of(new TableIdentifier("Product")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT (? - \"discount\") FROM \"Product\"");
        assertThat(result.parameters()).containsExactly(100);
    }

    @Test
    void selectWithArithmeticMultiplication() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ArithmeticExpression.multiplication(
                        ColumnReference.of("Order", "quantity"), ColumnReference.of("Product", "price")))))
                .from(From.of(new TableIdentifier("Order")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT (\"quantity\" * \"price\") FROM \"Order\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithArithmeticDivision() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        ArithmeticExpression.division(ColumnReference.of("Customer", "score"), Literal.of(2)))))
                .from(From.of(new TableIdentifier("Customer")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT (\"score\" / ?) FROM \"Customer\"");
        assertThat(result.parameters()).containsExactly(2);
    }

    @Test
    void selectWithArithmeticModulo() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        ArithmeticExpression.modulo(ColumnReference.of("User", "id"), Literal.of(10)))))
                .from(From.of(new TableIdentifier("User")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT (\"id\" % ?) FROM \"User\"");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void selectWithArithmeticNegation() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        ArithmeticExpression.negation(ColumnReference.of("Customer", "score")))))
                .from(From.of(new TableIdentifier("Customer")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT (-\"score\") FROM \"Customer\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithArithmeticNegationLiteral() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ArithmeticExpression.negation(Literal.of(100)))))
                .from(From.of(new TableIdentifier("Test")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT (-?) FROM \"Test\"");
        assertThat(result.parameters()).containsExactly(100);
    }

    @Test
    void selectWithCastLiteralToVarchar() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(Cast.of(Literal.of("hello"), "VARCHAR(50)"))))
                .from(From.of(new TableIdentifier("Test")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CAST(? AS VARCHAR(50)) FROM \"Test\"");
        assertThat(result.parameters()).containsExactly("hello");
    }

    @Test
    void selectWithCastColumnToInt() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(Cast.of(ColumnReference.of("users", "age"), "INT"))))
                .from(From.of(new TableIdentifier("Test")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CAST(\"age\" AS INT) FROM \"Test\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithCastNumberToDate() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(Cast.of(Literal.of(123), "DATE"))))
                .from(From.of(new TableIdentifier("Test")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CAST(? AS DATE) FROM \"Test\"");
        assertThat(result.parameters()).containsExactly(123);
    }

    @Test
    void selectWithConcatTwoLiterals() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(Concat.concat(Literal.of("Hello"), Literal.of("World")))))
                .from(From.of(new TableIdentifier("Test")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CONCAT(?, ?) FROM \"Test\"");
        assertThat(result.parameters()).containsExactly("Hello", "World");
    }

    @Test
    void selectWithConcatLiteralAndColumn() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        Concat.concat(Literal.of("Name: "), ColumnReference.of("users", "name")))))
                .from(From.of(new TableIdentifier("Test")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CONCAT(?, \"name\") FROM \"Test\"");
        assertThat(result.parameters()).containsExactly("Name: ");
    }

    @Test
    void selectWithConcatWithSeparator() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        Concat.concatWithSeparator(" - ", Literal.of("First"), Literal.of("Second")))))
                .from(From.of(new TableIdentifier("Test")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CONCAT_WS(?, ?, ?) FROM \"Test\"");
        assertThat(result.parameters()).containsExactly(" - ", "First", "Second");
    }

    @Test
    void selectWithCurrentDate() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(new CurrentDate())))
                .from(From.of(new TableIdentifier("Test")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CURRENT_DATE FROM \"Test\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithCurrentDateInComparison() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("orders", "created_date"))))
                .from(From.of(new TableIdentifier("orders")))
                .where(Where.of(Comparison.eq(ColumnReference.of("orders", "created_date"), new CurrentDate())))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT \"created_date\" FROM \"orders\" WHERE \"created_date\" = CURRENT_DATE");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithCurrentDateTime() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(new CurrentDateTime())))
                .from(From.of(new TableIdentifier("Test")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CURRENT_TIMESTAMP FROM \"Test\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithCurrentDateTimeInComparison() {
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("events", "timestamp"))))
                .from(From.of(new TableIdentifier("events")))
                .where(Where.of(Comparison.eq(ColumnReference.of("events", "timestamp"), new CurrentDateTime())))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql())
                .isEqualTo("SELECT \"timestamp\" FROM \"events\" WHERE \"timestamp\" = CURRENT_TIMESTAMP");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithDateAddition() {
        var interval = new Interval(Literal.of(30), Interval.IntervalUnit.DAY);
        var dateAdd = DateArithmetic.addition(ColumnReference.of("orders", "created_date"), interval);
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(dateAdd)))
                .from(From.of(new TableIdentifier("orders")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT DATEADD(INTERVAL ? DAY, \"created_date\") FROM \"orders\"");
        assertThat(result.parameters()).containsExactly(30);
    }

    @Test
    void selectWithDateSubtraction() {
        var interval = new Interval(Literal.of(7), Interval.IntervalUnit.DAY);
        var dateSub = DateArithmetic.subtraction(ColumnReference.of("events", "event_date"), interval);
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(dateSub)))
                .from(From.of(new TableIdentifier("events")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT DATESUB(INTERVAL ? DAY, \"event_date\") FROM \"events\"");
        assertThat(result.parameters()).containsExactly(7);
    }

    @Test
    void selectWithDateArithmeticSimple() {
        var interval = new Interval(Literal.of(1), Interval.IntervalUnit.MONTH);
        var dateAdd = DateArithmetic.addition(ColumnReference.of("subscriptions", "start_date"), interval);
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(dateAdd)))
                .from(From.of(new TableIdentifier("subscriptions")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT DATEADD(INTERVAL ? MONTH, \"start_date\") FROM \"subscriptions\"");
        assertThat(result.parameters()).containsExactly(1);
    }

    @Test
    void selectWithExtractYear() {
        var extractYear = ExtractDatePart.year(ColumnReference.of("orders", "created_date"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(extractYear)))
                .from(From.of(new TableIdentifier("orders")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT EXTRACT(YEAR FROM \"created_date\") FROM \"orders\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithExtractMonth() {
        var extractMonth = ExtractDatePart.month(ColumnReference.of("events", "event_date"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(extractMonth)))
                .from(From.of(new TableIdentifier("events")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT EXTRACT(MONTH FROM \"event_date\") FROM \"events\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithExtractDayInWhere() {
        var extractDay = ExtractDatePart.day(ColumnReference.of("logs", "timestamp"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("logs", "id"))))
                .from(From.of(new TableIdentifier("logs")))
                .where(Where.of(Comparison.eq(extractDay, Literal.of(25))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"logs\" WHERE EXTRACT(DAY FROM \"timestamp\") = ?");
        assertThat(result.parameters()).containsExactly(25);
    }

    @Test
    void selectWithLeftFunction() {
        var leftFunction = Left.of(ColumnReference.of("users", "full_name"), 5);
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(leftFunction)))
                .from(From.of(new TableIdentifier("users")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT LEFT(\"full_name\", ?) FROM \"users\"");
        assertThat(result.parameters()).containsExactly(5);
    }

    @Test
    void selectWithLeftLiteralString() {
        var leftFunction = Left.of(Literal.of("Hello World"), 3);
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(leftFunction)))
                .from(From.of(new TableIdentifier("test")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT LEFT(?, ?) FROM \"test\"");
        assertThat(result.parameters()).containsExactly("Hello World", 3);
    }

    @Test
    void selectWithLeftInSelect() {
        var leftFunction = Left.of(ColumnReference.of("products", "code"), 2);
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(leftFunction)))
                .from(From.of(new TableIdentifier("products")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT LEFT(\"code\", ?) FROM \"products\"");
        assertThat(result.parameters()).containsExactly(2);
    }

    @Test
    void selectWithLeftInWhere() {
        var leftFunction = Left.of(ColumnReference.of("products", "code"), 2);
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("products", "id"))))
                .from(From.of(new TableIdentifier("products")))
                .where(Where.of(Comparison.eq(leftFunction, Literal.of("AB"))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"products\" WHERE LEFT(\"code\", ?) = ?");
        assertThat(result.parameters()).containsExactly(2, "AB");
    }

    @Test
    void selectWithLengthFunction() {
        var lengthFunction = new Length(ColumnReference.of("users", "email"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(lengthFunction)))
                .from(From.of(new TableIdentifier("users")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT LENGTH(\"email\") FROM \"users\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithLengthLiteralString() {
        var lengthFunction = new Length(Literal.of("Test String"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(lengthFunction)))
                .from(From.of(new TableIdentifier("dummy")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT LENGTH(?) FROM \"dummy\"");
        assertThat(result.parameters()).containsExactly("Test String");
    }

    @Test
    void selectWithLengthInWhere() {
        var lengthFunction = new Length(ColumnReference.of("users", "username"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "id"))))
                .from(From.of(new TableIdentifier("users")))
                .where(Where.of(Comparison.gt(lengthFunction, Literal.of(8))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"users\" WHERE LENGTH(\"username\") > ?");
        assertThat(result.parameters()).containsExactly(8);
    }

    @Test
    void selectWithCharLengthFunction() {
        var charLengthFunction = new CharLength(ColumnReference.of("users", "email"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(charLengthFunction)))
                .from(From.of(new TableIdentifier("users")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CHAR_LENGTH(\"email\") FROM \"users\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithCharLengthLiteralString() {
        var charLengthFunction = new CharLength(Literal.of("Test String"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(charLengthFunction)))
                .from(From.of(new TableIdentifier("dummy")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CHAR_LENGTH(?) FROM \"dummy\"");
        assertThat(result.parameters()).containsExactly("Test String");
    }

    @Test
    void selectWithCharLengthInWhere() {
        var charLengthFunction = new CharLength(ColumnReference.of("products", "code"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("products", "id"))))
                .from(From.of(new TableIdentifier("products")))
                .where(Where.of(Comparison.eq(charLengthFunction, Literal.of(10))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"products\" WHERE CHAR_LENGTH(\"code\") = ?");
        assertThat(result.parameters()).containsExactly(10);
    }

    @Test
    void selectWithCharacterLengthFunction() {
        var characterLengthFunction = new CharacterLength(ColumnReference.of("users", "full_name"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(characterLengthFunction)))
                .from(From.of(new TableIdentifier("users")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CHARACTER_LENGTH(\"full_name\") FROM \"users\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectWithCharacterLengthLiteralString() {
        var characterLengthFunction = new CharacterLength(Literal.of("Test String"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(characterLengthFunction)))
                .from(From.of(new TableIdentifier("dummy")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT CHARACTER_LENGTH(?) FROM \"dummy\"");
        assertThat(result.parameters()).containsExactly("Test String");
    }

    @Test
    void selectWithCharacterLengthInWhere() {
        var characterLengthFunction = new CharacterLength(ColumnReference.of("comments", "content"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("comments", "id"))))
                .from(From.of(new TableIdentifier("comments")))
                .where(Where.of(Comparison.lt(characterLengthFunction, Literal.of(280))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"comments\" WHERE CHARACTER_LENGTH(\"content\") < ?");
        assertThat(result.parameters()).containsExactly(280);
    }

    @Test
    void selectModFunction() {
        var modFunction = new Mod(ColumnReference.of("orders", "total"), Literal.of(100));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(modFunction)))
                .from(From.of(new TableIdentifier("orders")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT MOD(\"total\", ?) FROM \"orders\"");
        assertThat(result.parameters()).containsExactly(100);
    }

    @Test
    void selectModWithTwoLiterals() {
        var modFunction = new Mod(Literal.of(17), Literal.of(5));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(modFunction)))
                .from(From.of(new TableIdentifier("dummy")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT MOD(?, ?) FROM \"dummy\"");
        assertThat(result.parameters()).containsExactly(17, 5);
    }

    @Test
    void whereModEqualsZero() {
        var modFunction = new Mod(ColumnReference.of("numbers", "value"), Literal.of(3));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("numbers", "id"))))
                .from(From.of(new TableIdentifier("numbers")))
                .where(Where.of(Comparison.eq(modFunction, Literal.of(0))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"numbers\" WHERE MOD(\"value\", ?) = ?");
        assertThat(result.parameters()).containsExactly(3, 0);
    }

    @Test
    void selectNullScalarExpression() {
        var nullExpression = new NullScalarExpression();
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(nullExpression)))
                .from(From.of(new TableIdentifier("dummy")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT NULL FROM \"dummy\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void whereNullScalarExpressionComparison() {
        var nullExpression = new NullScalarExpression();
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "id"))))
                .from(From.of(new TableIdentifier("users")))
                .where(Where.of(Comparison.eq(ColumnReference.of("users", "status"), nullExpression)))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"users\" WHERE \"status\" = NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void selectPowerFunction() {
        var powerFunction = new Power(Literal.of(2), Literal.of(8));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(powerFunction)))
                .from(From.of(new TableIdentifier("dummy")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT POWER(?, ?) FROM \"dummy\"");
        assertThat(result.parameters()).containsExactly(2, 8);
    }

    @Test
    void selectPowerWithColumn() {
        var powerFunction = new Power(ColumnReference.of("math", "base"), Literal.of(3));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(powerFunction)))
                .from(From.of(new TableIdentifier("math")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT POWER(\"base\", ?) FROM \"math\"");
        assertThat(result.parameters()).containsExactly(3);
    }

    @Test
    void wherePowerGreaterThan() {
        var powerFunction = new Power(ColumnReference.of("calculations", "value"), Literal.of(2));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("calculations", "id"))))
                .from(From.of(new TableIdentifier("calculations")))
                .where(Where.of(Comparison.gt(powerFunction, Literal.of(100))))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"calculations\" WHERE POWER(\"value\", ?) > ?");
        assertThat(result.parameters()).containsExactly(2, 100);
    }

    @Test
    void selectReplaceFunction() {
        var replaceFunction = new Replace(Literal.of("Hello World"), Literal.of("World"), Literal.of("Universe"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(replaceFunction)))
                .from(From.of(new TableIdentifier("dummy")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT REPLACE(?, ?, ?) FROM \"dummy\"");
        assertThat(result.parameters()).containsExactly("Hello World", "World", "Universe");
    }

    @Test
    void selectReplaceWithColumn() {
        var replaceFunction =
                new Replace(ColumnReference.of("users", "email"), Literal.of("@old.com"), Literal.of("@new.com"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(replaceFunction)))
                .from(From.of(new TableIdentifier("users")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT REPLACE(\"email\", ?, ?) FROM \"users\"");
        assertThat(result.parameters()).containsExactly("@old.com", "@new.com");
    }

    @Test
    void whereReplaceContains() {
        var replaceFunction = new Replace(ColumnReference.of("content", "text"), Literal.of("old"), Literal.of("new"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("content", "id"))))
                .from(From.of(new TableIdentifier("content")))
                .where(Where.of(new Like(replaceFunction, "%new%")))
                .build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());
        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"content\" WHERE REPLACE(\"text\", ?, ?) LIKE ?");
        assertThat(result.parameters()).containsExactly("old", "new", "%new%");
    }

    @Test
    void createTableStatement() {
        CreateTableStatement createTable = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("users"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build()))
                .build());

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(createTable, new AstContext());

        assertThat(result.sql()).startsWith("CREATE TABLE");
        assertThat(result.sql()).contains("users");
        assertThat(result.sql()).contains("id");
        assertThat(result.sql()).contains("name");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void createTableStatementWithUniqueConstraint() {
        CreateTableStatement createTable = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("users"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("email").build()))
                .constraint(new ConstraintDefinition.UniqueConstraintDefinition("email"))
                .build());

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(createTable, new AstContext());

        assertThat(result.sql()).startsWith("CREATE TABLE");
        assertThat(result.sql()).contains("UNIQUE");
        assertThat(result.sql()).contains("email");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void createTableStatementWithForeignKeyConstraint() {
        ReferencesItem references = new ReferencesItem("users", "id");
        CreateTableStatement createTable = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("orders"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.integer("user_id").build()))
                .constraint(new ConstraintDefinition.ForeignKeyConstraintDefinition(List.of("user_id"), references))
                .build());

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(createTable, new AstContext());

        assertThat(result.sql()).startsWith("CREATE TABLE");
        assertThat(result.sql()).contains("FOREIGN KEY");
        assertThat(result.sql()).contains("user_id");
        assertThat(result.sql()).contains("REFERENCES");
        assertThat(result.sql()).contains("users");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void createTableStatementWithCheckConstraint() {
        Comparison ageCheck = Comparison.gt(ColumnReference.of("", "age"), Literal.of(18));
        CreateTableStatement createTable = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("users"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.integer("age").build()))
                .constraint(new ConstraintDefinition.CheckConstraintDefinition(ageCheck))
                .build());

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(createTable, new AstContext());

        assertThat(result.sql()).startsWith("CREATE TABLE");
        assertThat(result.sql()).contains("CHECK");
        assertThat(result.sql()).contains("age");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void defaultConstraintIntegration() {
        ConstraintDefinition.DefaultConstraintDefinition constraint =
                new ConstraintDefinition.DefaultConstraintDefinition(Literal.of("active"));
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();

        PsDto result = renderer.visit(constraint, new AstContext());

        assertThat(result.sql()).isEqualTo("DEFAULT 'active'");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void scalarSubqueryIntegration() {
        SelectStatement innerSelect = SelectStatement.builder()
                .select(Select.of(new AggregateCallProjection(AggregateCall.countStar())))
                .from(From.of(new TableIdentifier("users")))
                .where(Where.of(Comparison.eq(ColumnReference.of("users", "active"), Literal.of(true))))
                .build();

        ScalarSubquery subquery =
                ScalarSubquery.builder().tableExpression(innerSelect).build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(subquery, new AstContext());

        assertThat(result.sql()).startsWith("(");
        assertThat(result.sql()).endsWith(")");
        assertThat(result.sql()).contains("SELECT COUNT(*)");
        assertThat(result.sql()).contains("FROM \"users\"");
        assertThat(result.sql()).contains("WHERE");
        assertThat(result.parameters()).containsExactly(true);
    }

    @Test
    void roundInSelectClause() {
        Round roundFunction = new Round(ColumnReference.of("products", "price"), Literal.of(2));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(roundFunction, new Alias("rounded_price"))))
                .from(From.of(new TableIdentifier("products")))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql()).isEqualTo("SELECT ROUND(\"price\", ?) AS \"rounded_price\" FROM \"products\"");
        assertThat(result.parameters()).containsExactly(2);
    }

    @Test
    void roundInWhereClause() {
        Round roundFunction = Round.of(ColumnReference.of("orders", "total"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("orders", "id"))))
                .from(From.of(new TableIdentifier("orders")))
                .where(Where.of(Comparison.gt(roundFunction, Literal.of(100))))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"orders\" WHERE ROUND(\"total\") > ?");
        assertThat(result.parameters()).containsExactly(100);
    }

    @Test
    void roundWithMixedParameters() {
        Round roundFunction = new Round(Literal.of(3.14159), ColumnReference.of("settings", "precision"));
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(roundFunction, new Alias("pi_rounded"))))
                .from(From.of(new TableIdentifier("settings")))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql()).isEqualTo("SELECT ROUND(?, \"precision\") AS \"pi_rounded\" FROM \"settings\"");
        assertThat(result.parameters()).containsExactly(3.14159);
    }

    @Test
    void substringInSelectClause() {
        Substring substringFunction = Substring.of(ColumnReference.of("users", "name"), 1, 10);
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(substringFunction, new Alias("short_name"))))
                .from(From.of(new TableIdentifier("users")))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql()).isEqualTo("SELECT SUBSTRING(\"name\", ?, ?) AS \"short_name\" FROM \"users\"");
        assertThat(result.parameters()).containsExactly(1, 10);
    }

    @Test
    void substringInWhereClause() {
        Substring substringFunction = Substring.of(ColumnReference.of("posts", "content"), 1, 50);
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("posts", "id"))))
                .from(From.of(new TableIdentifier("posts")))
                .where(Where.of(new Like(substringFunction, "%search%")))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"posts\" WHERE SUBSTRING(\"content\", ?, ?) LIKE ?");
        assertThat(result.parameters()).containsExactly(1, 50, "%search%");
    }

    @Test
    void substringWithoutLength() {
        Substring substringFunction = Substring.of(Literal.of("Hello World"), 7);
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(substringFunction, new Alias("greeting"))))
                .from(From.of(new TableIdentifier("dual")))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql()).isEqualTo("SELECT SUBSTRING(?, ?) AS \"greeting\" FROM \"dual\"");
        assertThat(result.parameters()).containsExactly("Hello World", 7);
    }

    @Test
    void trimInSelectClause() {
        var trimFunction = Trim.trim(ColumnReference.of("users", "name"));
        var selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(trimFunction, new Alias("clean_name"))))
                .from(From.of(new TableIdentifier("users")))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql()).isEqualTo("SELECT TRIM(\"name\") AS \"clean_name\" FROM \"users\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void trimInWhereClause() {
        var trimFunction = Trim.trimBoth(ColumnReference.of("products", "code"));
        var selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("products", "id"))))
                .from(From.of(new TableIdentifier("products")))
                .where(Where.of(Comparison.eq(trimFunction, Literal.of("ABC123"))))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"products\" WHERE TRIM(BOTH \"code\") = ?");
        assertThat(result.parameters()).containsExactly("ABC123");
    }

    @Test
    void trimWithCharactersToRemove() {
        var trimFunction = Trim.trimLeading(Literal.of("*"), Literal.of("***data***"));
        var selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(trimFunction, new Alias("cleaned"))))
                .from(From.of(new TableIdentifier("dual")))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql()).isEqualTo("SELECT TRIM(LEADING ? FROM ?) AS \"cleaned\" FROM \"dual\"");
        assertThat(result.parameters()).containsExactly("*", "***data***");
    }

    @Test
    void unaryNumericInSelectClause() {
        var absFunction = UnaryNumeric.abs(ColumnReference.of("transactions", "amount"));
        var selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(absFunction, new Alias("abs_amount"))))
                .from(From.of(new TableIdentifier("transactions")))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql()).isEqualTo("SELECT ABS(\"amount\") AS \"abs_amount\" FROM \"transactions\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void unaryNumericInWhereClause() {
        var sqrtFunction = UnaryNumeric.sqrt(Literal.of(16));
        var selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("data", "id"))))
                .from(From.of(new TableIdentifier("data")))
                .where(Where.of(Comparison.gt(ColumnReference.of("data", "value"), sqrtFunction)))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"data\" WHERE \"value\" > SQRT(?)");
        assertThat(result.parameters()).containsExactly(16);
    }

    @Test
    void unaryNumericMultipleFunctions() {
        var ceilFunction = UnaryNumeric.ceil(ColumnReference.of("orders", "total"));
        var floorFunction = UnaryNumeric.floor(Literal.of(9.8));
        var selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ceilFunction, new Alias("rounded_up")),
                        new ScalarExpressionProjection(floorFunction, new Alias("rounded_down"))))
                .from(From.of(new TableIdentifier("orders")))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql())
                .isEqualTo("SELECT CEIL(\"total\") AS \"rounded_up\", FLOOR(?) AS \"rounded_down\" FROM \"orders\"");
        assertThat(result.parameters()).containsExactly(9.8);
    }

    @Test
    void unaryStringInSelectClause() {
        var upperFunction = UnaryString.upper(ColumnReference.of("users", "name"));
        var selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(upperFunction, new Alias("upper_name"))))
                .from(From.of(new TableIdentifier("users")))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql()).isEqualTo("SELECT UPPER(\"name\") AS \"upper_name\" FROM \"users\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void unaryStringInWhereClause() {
        var lowerFunction = UnaryString.lower(Literal.of("ADMIN"));
        var selectStmt = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "id"))))
                .from(From.of(new TableIdentifier("users")))
                .where(Where.of(Comparison.eq(UnaryString.lower(ColumnReference.of("users", "role")), lowerFunction)))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql()).isEqualTo("SELECT \"id\" FROM \"users\" WHERE LOWER(\"role\") = LOWER(?)");
        assertThat(result.parameters()).containsExactly("ADMIN");
    }

    @Test
    void unaryStringMixedCaseFunctions() {
        var upperFunction = UnaryString.upper(Literal.of("hello"));
        var lowerFunction = UnaryString.lower(ColumnReference.of("products", "category"));
        var selectStmt = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(upperFunction, new Alias("greeting")),
                        new ScalarExpressionProjection(lowerFunction, new Alias("lower_category"))))
                .from(From.of(new TableIdentifier("products")))
                .build();

        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto result = renderer.visit(selectStmt, new AstContext());

        assertThat(result.sql())
                .isEqualTo(
                        "SELECT UPPER(?) AS \"greeting\", LOWER(\"category\") AS \"lower_category\" FROM \"products\"");
        assertThat(result.parameters()).containsExactly("hello");
    }
}
