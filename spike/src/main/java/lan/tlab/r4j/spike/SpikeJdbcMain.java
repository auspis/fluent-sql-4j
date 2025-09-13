package lan.tlab.r4j.spike;

public class SpikeJdbcMain {
    /*
     I tipi supportati da PreparedStatement per i parametri dipendono dal metodo setXxx usato. I più comuni sono:
       * setInt, setLong, setShort, setByte (numeri interi)
       * setFloat, setDouble, setBigDecimal (numeri decimali)
       * setString (stringhe)
       * setBoolean
       * setDate, setTime, setTimestamp (date e orari, da java.sql)
       * setBytes (array di byte)
       * setObject (per tipi generici, incluso null)
       * setNull (per valori null specificando il tipo SQL)

     Alcuni driver JDBC supportano anche tipi specifici come setArray, setBlob, setClob, ecc.
    */

    public static void main(String[] args) throws Exception {
        // 1. Open H2 in-memory connection
        try (var conn = java.sql.DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")) {
            try (var stmt = conn.createStatement()) {
                // 2. Create table
                stmt.executeUpdate(
                        "CREATE TABLE \"users\" (\"id\" INT PRIMARY KEY, \"name\" VARCHAR(100), \"email\" VARCHAR(100))");
                System.out.println("Table users created.");
            }
            // 3. Insert a user
            try (var ps =
                    conn.prepareStatement("INSERT INTO \"users\" (\"id\", \"name\", \"email\") VALUES (?, ?, ?)"); ) {
                ps.setInt(1, 1);
                ps.setString(2, "Alice");
                ps.setString(3, "alice@example.com");
                ps.executeUpdate();
                System.out.println("Inserted user Alice.");
            }
            // 4. Update the user's email
            try (var ps = conn.prepareStatement("UPDATE \"users\" SET \"email\" = ? WHERE \"id\" = ?")) {
                ps.setString(1, "alice@newdomain.com");
                ps.setInt(2, 1);
                ps.executeUpdate();
                System.out.println("Updated Alice's email.");
            }
            // 5. Query and print user
            try (var rs = conn.createStatement().executeQuery("SELECT * FROM \"users\" WHERE \"id\" = 1")) {
                if (rs.next()) {
                    System.out.printf(
                            "User: id=%d, name=%s, email=%s\n",
                            rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                }
            }
            // 6. Delete the user
            try (var ps = conn.prepareStatement("DELETE FROM \"users\" WHERE \"id\" = ?")) {
                ps.setInt(1, 1);
                ps.executeUpdate();
                System.out.println("Deleted user Alice.");
            }
            // 7. Verify deletion
            try (var rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM \"users\"")) {
                if (rs.next()) {
                    System.out.println("User count after deletion: " + rs.getInt(1));
                }
            }
        }
    }
}
