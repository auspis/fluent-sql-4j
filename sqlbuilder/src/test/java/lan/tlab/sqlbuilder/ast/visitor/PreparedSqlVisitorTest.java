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
}
