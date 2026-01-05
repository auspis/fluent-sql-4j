# JDSQL MySQL Plugin

Dialect plugin providing MySQL-specific functionality for the JDSQL SQL builder.

## Supported Versions

- **MySQL 8.0+**

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.auspis.fluentsql4j</groupId>
    <artifactId>plugin-mysql</artifactId>
    <version>${fluentsql4j.version}</version>
</dependency>
```

The plugin is automatically discovered via Java ServiceLoader.

## Usage

### Getting the MySQL DSL

```java
import io.github.auspis.fluentsql4j.dsl.DSLRegistry;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.MysqlDSL;

DSLRegistry registry = DSLRegistry.createWithServiceLoader();
MysqlDSL mysql = registry.dslFor("mysql", "8.0.35", MysqlDSL.class).orElseThrow();
```

## MySQL-Specific Features

### GROUP_CONCAT

MySQL's `GROUP_CONCAT` function aggregates strings with custom separators and ordering.

#### Basic Usage

```java
PreparedStatement ps = mysql.select()
    .column("department")
    .expression(
        mysql.groupConcat("name")
            .separator(", ")
            .build()
    ).as("employee_names")
    .from("employees")
    .groupBy("department")
    .build(connection);
```

**Generated SQL:**

```sql
SELECT "employees"."department",
       GROUP_CONCAT("employees"."name" SEPARATOR ', ') AS employee_names
FROM "employees"
GROUP BY "employees"."department"
```

#### Advanced Usage with DISTINCT and ORDER BY

```java
PreparedStatement ps = mysql.select()
    .column("category")
    .expression(
        mysql.groupConcat("product_name")
            .distinct()
            .orderBy("price DESC")
            .separator(" | ")
            .build()
    ).as("top_products")
    .from("products")
    .groupBy("category")
    .build(connection);
```

**Generated SQL:**

```sql
SELECT "products"."category",
       GROUP_CONCAT(DISTINCT "products"."product_name" ORDER BY price DESC SEPARATOR ' | ') AS top_products
FROM "products"
GROUP BY "products"."category"
```

#### Complex Query Example

```java
PreparedStatement ps = mysql.select()
    .column("department")
    .expression(
        mysql.groupConcat("name")
            .orderBy("salary DESC")
            .separator(", ")
            .build()
    ).as("employees_by_salary")
    .expression(AggregateCall.avg(ColumnReference.of("employees", "salary")))
        .as("avg_salary")
    .expression(AggregateCall.count(ColumnReference.of("employees", "id")))
        .as("employee_count")
    .from("employees")
    .groupBy("department")
    .having()
        .column("employee_count")
        .gt(5)
    .orderBy("avg_salary DESC")
    .build(connection);
```

**Generated SQL:**

```sql
SELECT "employees"."department",
       GROUP_CONCAT("employees"."name" ORDER BY salary DESC SEPARATOR ', ') AS employees_by_salary,
       AVG("employees"."salary") AS avg_salary,
       COUNT("employees"."id") AS employee_count
FROM "employees"
GROUP BY "employees"."department"
HAVING COUNT("employees"."id") > 5
ORDER BY avg_salary DESC
```

### Combining Standard and MySQL-Specific Features

You can use both standard SQL features and MySQL-specific functions:

```java
// Use both standard window functions and MySQL GROUP_CONCAT
PreparedStatement ps = mysql.select()
    .column("region")
    .rowNumber()
        .partitionBy("region")
        .orderByDesc("total_sales")
        .as("region_rank")
    .expression(
        mysql.groupConcat("store_name")
            .orderBy("total_sales DESC")
            .separator(" > ")
            .build()
    ).as("top_stores")
    .expression(AggregateCall.sum(ColumnReference.of("stores", "total_sales")))
        .as("region_total")
    .from("stores")
    .groupBy("region")
    .build(connection);
```

**Generated SQL:**

```sql
SELECT "stores"."region",
       ROW_NUMBER() OVER (PARTITION BY "stores"."region" ORDER BY "stores"."total_sales" DESC) AS region_rank,
       GROUP_CONCAT("stores"."store_name" ORDER BY total_sales DESC SEPARATOR ' > ') AS top_stores,
       SUM("stores"."total_sales") AS region_total
FROM "stores"
GROUP BY "stores"."region"
```

## Additional Resources

- **[Core Documentation](../../core/)**: SQL AST and plugin architecture
- **[DSL Usage Guide](../../data/wiki/DSL_USAGE_GUIDE.md)**: Complete DSL reference
- **[Plugin Development Guide](../../data/wiki/PLUGIN_DEVELOPMENT.md)**: How to create custom plugins

