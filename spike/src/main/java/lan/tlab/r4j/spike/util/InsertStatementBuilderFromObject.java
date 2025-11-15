package lan.tlab.r4j.spike.util;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.Expression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.InsertValues;
import lan.tlab.r4j.jdsql.ast.dml.statement.InsertStatement;

public class InsertStatementBuilderFromObject {
    public static InsertStatement fromObject(String tableName, Object obj) {
        Class<?> clazz = obj.getClass();
        List<ColumnReference> columns = new ArrayList<>();
        List<Expression> values = new ArrayList<>();
        if (clazz.isRecord()) {
            for (RecordComponent rc : clazz.getRecordComponents()) {
                columns.add(ColumnReference.of("", rc.getName()));
                try {
                    Object value = rc.getAccessor().invoke(obj);
                    values.add(Literal.of(value.toString()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            throw new IllegalArgumentException("Only records are supported in this version");
        }
        return InsertStatement.builder()
                .table(new TableIdentifier(tableName))
                .columns(columns)
                .data(new InsertValues(values))
                .build();
    }
}
