package lan.tlab.r4j.spike;

import java.util.List;
import lan.tlab.r4j.spike.util.InsertStatementBuilderFromObject;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition.ColumnDefinitionBuilder;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.statement.CreateTableStatement;
import lan.tlab.sqlbuilder.ast.statement.InsertStatement;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.PreparedSqlResult;
import lan.tlab.sqlbuilder.ast.visitor.PreparedSqlVisitor;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;

public class SpikeObjectToJdbcMain {

    public static void main(String[] args) throws Exception {
        SqlRendererImpl sqlRenderer = SqlRendererFactory.standardSql2008();
        TableDefinition userTableDefinition = TableDefinition.builder()
                .table(new Table("User"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build(),
                        ColumnDefinitionBuilder.varchar("email").build()))
                .build();

        // 1. Open H2 in-memory connection
        try (var conn = java.sql.DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")) {
            try (var stmt = conn.createStatement()) {
                // 2. Create table
                //                stmt.executeUpdate("CREATE TABLE \"users\" (\"id\" INT PRIMARY KEY, \"name\"
                // VARCHAR(100), \"email\" VARCHAR(100))");
                CreateTableStatement statement = new CreateTableStatement(userTableDefinition);
                stmt.executeUpdate(sqlRenderer.visit(statement, new AstContext()));
                System.out.println("Table User created.");
            }
            // 3. Insert a user
            User user = new User(1, "John", "john@example.com");
            InsertStatement insertStatement = InsertStatementBuilderFromObject.fromObject("User", user);
            PreparedSqlVisitor preparedVisitor = new PreparedSqlVisitor();
            PreparedSqlResult preparedResult = preparedVisitor.visit(insertStatement, new AstContext());
            String insertSql = preparedResult.sql();
            System.out.println("SQL insert: " + insertSql);
            try (var ps = conn.prepareStatement(insertSql)) {
                // Set parameters from PreparedSqlResult
                var params = preparedResult.parameters();
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                ps.executeUpdate();
                System.out.println("Inserted user: " + user);
            }

            // verify

            System.out.println("-------------------- VERIFY ---------------------");
            try (var rs = conn.createStatement().executeQuery("SELECT * FROM \"User\" WHERE \"id\" = 1")) {
                if (rs.next()) {
                    System.out.printf(
                            "User: id=%d, name=%s, email=%s\n",
                            rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                }
            }

            // SELECT example using PreparedSqlVisitor
            System.out.println("-------------------- SELECT SPIKE ---------------------");
            // Build SELECT statement: SELECT "id", "name" FROM "User" WHERE "id" = ?
            SelectStatement selectStmt = SelectStatement.builder()
                    .select(Select.of(
                            new ScalarExpressionProjection(ColumnReference.of("User", "id")),
                            new ScalarExpressionProjection(ColumnReference.of("User", "name"))))
                    .from(From.of(new Table("User")))
                    .where(Where.of(Comparison.eq(ColumnReference.of("User", "id"), Literal.of(1))))
                    .build();
            PreparedSqlVisitor selectVisitor = new PreparedSqlVisitor();
            PreparedSqlResult selectResult = selectVisitor.visit(selectStmt, new AstContext());
            String selectSql = selectResult.sql();
            System.out.println("SQL select: " + selectSql);
            System.out.println("Parameters: " + selectResult.parameters());
            try (var ps = conn.prepareStatement(selectSql)) {
                var params = selectResult.parameters();
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.printf("User: id=%d, name=%s\n", rs.getInt("id"), rs.getString("name"));
                    }
                }
            }
        }
    }
}
