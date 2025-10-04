package lan.tlab.r4j.integration.sql;

// @Testcontainers
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StandardSqlRendererPostgresIT {
    //    @Container
    //    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2")
    //            .withDatabaseName("testdb")
    //            .withUsername("test")
    //            .withPassword("test");
    //
    //    private Connection connection;
    //    private SqlRenderer renderer;
    //
    //    @BeforeAll
    //    void setUp() throws Exception {
    //        postgres.start();
    //        connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(),
    // postgres.getPassword());
    //        renderer = SqlRendererFactory.standardSql2008();
    //        try (Statement stmt = connection.createStatement()) {
    //            stmt.execute("CREATE TABLE Customer (id INT PRIMARY KEY, name VARCHAR(255))");
    //            stmt.execute("INSERT INTO Customer (id, name) VALUES (1, 'Alice'), (2, 'Bob')");
    //        }
    //    }
    //
    //    @AfterAll
    //    void tearDown() throws Exception {
    //        connection.close();
    //        postgres.stop();
    //    }
    //
    //    @Test
    //    void testSelectFromCustomer() throws Exception {
    //        SelectStatement statement = SelectStatement.builder()
    //                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("Customer", "name"))))
    //                .from(From.fromTable("Customer"))
    //                .build();
    //        String sql = statement.accept(renderer);
    //        try (PreparedStatement ps = connection.prepareStatement(sql)) {
    //            ResultSet rs = ps.executeQuery();
    //            Assertions.assertTrue(rs.next());
    //            Assertions.assertEquals("Alice", rs.getString(1));
    //            Assertions.assertTrue(rs.next());
    //            Assertions.assertEquals("Bob", rs.getString(1));
    //            Assertions.assertFalse(rs.next());
    //        }
    //    }
}
