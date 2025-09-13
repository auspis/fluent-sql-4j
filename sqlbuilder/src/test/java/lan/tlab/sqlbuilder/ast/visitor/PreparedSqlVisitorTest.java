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
}
