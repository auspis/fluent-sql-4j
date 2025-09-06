package lan.tlab.sqlbuilder.ast.expression.item.ddl;

import java.util.List;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lan.tlab.sqlbuilder.ast.visitor.Visitable;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Singular;

@Builder
@Getter
public class ColumnDefinition implements Visitable {

    private static final String YYYY_MM_DD = "YYYY-MM-dd";

    private final String name;
    @Singular
    private final List<Constraint> constraints;

    @Default
    private final Type type = Type.VARCHAR;

    // TODO: aaa - move the following fields in a "ColumnMetadata" class
    @Deprecated
    private final String businessName;

    @Deprecated
    @Default
    private final String businessFormat = "";

    public enum Type {
        // numbers
        INTEGER, SMALLINT, BIGINT, DECIMAL, NUMERIC, FLOAT, REAL, DOUBLE_PRECISION,
        // strings
        CHAR, VARCHAR, CHARACTER, CHARACTER_VARYING,
        // binary
        BINARY, VARBINARY,
        // dates
        DATE, TIME, TIMESTAMP, TIME_WITH_TIME_ZONE, TIMESTAMP_WITH_TIME_ZONE,
        // boolean
        BOOLEAN,
        // others
        INTERVAL, XML, ARRAY, MULTISET, ROW, UDT
    }
    
    public static ColumnDefinition nullObject() {
        return builder().build();
    }

    public static ColumnDefinition string(String name) {
        return columnBuilder(name, name, Type.VARCHAR).build();
    }

    public static ColumnDefinition string(String businessName, String name) {
        return columnBuilder(businessName, name, Type.VARCHAR).build();
    }

    public static ColumnDefinition integer(String name) {
        return columnBuilder(name, name, Type.INTEGER).build();
    }

    public static ColumnDefinition date(String name) {
        return columnBuilder(name, name, Type.DATE).businessFormat(YYYY_MM_DD).build();
    }

    private static ColumnDefinitionBuilder columnBuilder(String businessName, String name, Type type) {
        return builder().businessName(businessName).name(name).type(type);
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
