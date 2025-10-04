## AST and the SQL Categories

An **Abstract Syntax Tree (AST)** is a fundamental data structure in computer science, used by compilers and interpreters to represent the syntactic structure of source code. It is a hierarchical, tree-like structure where each node denotes a construct occurring in the source code.

For SQL, the AST acts as an essential intermediary representation of a query. It **removes ambiguity** and irrelevant details (like parentheses, semicolons, and whitespace), focusing solely on the structural and conceptual components of the statement.

SQL statements are broadly categorized by their function, and the AST naturally groups these operations:

* **Data Definition Language (DDL):** Statements that **define or modify the structure** of database objects (e.g., `CREATE`, `DROP`).
* **Data Manipulation Language (DML):** Statements that **modify data** within the objects (e.g., `INSERT`, `UPDATE`, `DELETE`).
* **Data Query Language (DQL):** Statements used for **retrieving data** (e.g., `SELECT`).
* **Data Control Language (DCL):** Statements used to **control access rights** to the data and objects (e.g., `GRANT`, `REVOKE`).

The AST structure begins with a **Statement** node, classified by one of these four types, which then branches out to define the query's full logic.

---

## Core Structure and Grouping

The SQL AST is built upon a hierarchical model where every query begins with a **Statement** node, corresponding to the categories defined above.

| Category (Grouping) | Core Statement Nodes                                        | Purpose in the AST                                                                                                   |
|:--------------------|:------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------|
| **DQL**             | **`Select Statement`**.                                     | The primary node for data retrieval; it often forms the most complex subtrees due to the number of possible clauses. |
| **DML**             | `Insert Statement`, `Update Statement`, `Delete Statement`. | Nodes representing operations that modify data within existing database objects.                                     |
| **DDL**             | `Create Statement`, `Drop Statement`, `Alter Statement`.    | Nodes representing operations that define or modify the structure of database objects (schemas, tables, indexes).    |
| **DCL**             | `Grant Statement`, `Revoke Statement`.                      | Nodes for managing access rights and permissions on database objects.                                                |

Below the statement level, the AST branches into the following functional units:

| Functional Component | Example Nodes                                                                              | Role in the Query Structure                                                                                      |
|:---------------------|:-------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------|
| **Clauses**          | `Projection Clause` (SELECT list), `Source Clause` (FROM), `Filter Clause` (WHERE/HAVING). | Structural nodes that define the major sections and organization of DML/DQL logic.                               |
| **Predicates**       | `Equals`, `LessThan`, `And`, `Or`, `In predicate`.                                         | Nodes that **evaluate to a boolean value** (TRUE/FALSE) used in conditional logic.                               |
| **Expressions**      | `Addition`, `Sum Function`, `Case Expression`.                                             | Nodes that **evaluate to a scalar value** (number, string, date), forming the basis of calculations and results. |
| **Catalog Objects**  | `Table Identifier`, `Column Identifier`, `Schema Identifier`.                              | References to database entities, forming the terminal leaves that define the data sources or targets.            |

---

## Relationships Within the AST

The connectivity (edges) in the AST defines its coherence and structure, relying on **Composition** (structural grouping) and **Inheritance** (type hierarchy).

### 1. Structural Relationships (Composition)

These relationships define the parent-child structure and are critical for preserving the query's meaning and execution order:

* **Statement-to-Clause:** A high-level **Statement** node is linked to its constituent **Clause** nodes. The relationships are often named properties (e.g., `sql:from`, `sql:where`) to clarify their grammatical role.
* **Argument Ordering:** Within **Expressions**, **Predicates**, or **Identifiers**, child nodes (arguments/operands) are stored in an **ordered list** (often referred to as `sql:args`). This ordering is crucial for correct interpretation (e.g., a function's arguments must be in the correct sequence).
* **Hierarchical Identifiers:** A qualified reference (e.g., `schema.table.column`) is represented as an ordered sequence of identifier nodes, with the order determining the scope of the reference.

### 2. Hierarchical Relationships (Inheritance)

Conceptual groupings are maintained through an inheritance structure (a textual class hierarchy), ensuring that specific constructs are correctly classified under broader types:

* `Statement` $\rightarrow$ `Data Definition Statement` (DDL) $\rightarrow$ **`Create Statement`**.
* `Expression` $\rightarrow$ `Operator` $\rightarrow$ `Logical Operator` $\rightarrow$ **`And`**.

In summary, the SQL AST transforms a raw SQL query into a precise, navigable graph where the nodes are the language concepts (grouped by DDL, DML, DQL, DCL) and the ordered edges dictate the exact logical and structural relationships between them.

sources: [AST](https://ns.inria.fr/ast/sql/index.html)
