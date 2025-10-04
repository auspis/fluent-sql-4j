# SQL Abstract Syntax Tree (AST)

An **Abstract Syntax Tree (AST)** for SQL is a hierarchical, tree-like structure that represents the syntactic structure of an SQL query. It abstracts away non-essential elements like parentheses, semicolons, and whitespace (the concrete syntax), focusing solely on the **conceptual content** and **relationships** within the statement.

***

## Components and Grouping

SQL AST nodes are derived from a common root (e.g., **`Generic AST Node`**) and are grouped into major categories based on their function in the SQL language, defining the structure of the tree.

| Category (Grouping)               | Core Components (Nodes)                                                                                                                         | Description                                                                             |
|:----------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------|
| **Statements**                    | **`Select Statement`**, `Insert Statement`, `Update Statement`, `Delete Statement`, `Create Statement`.                                         | The **root of the tree**; defines the type of action to be executed (DML, DDL).         |
| **Clauses**                       | Nodes for the structural parts of a Statement, e.g., `Projection Clause` (SELECT list), `Source Clause` (FROM), `Filter Clause` (WHERE/HAVING). | Define the major segments of the statement and organize other components.               |
| **Predicates**                    | **`Comparison Operators`** (`Equals`, `LessThan`), **`Logical Operators`** (`And`, `Or`), `In predicate`, `Join Predicates`.                    | Components that **evaluate to a boolean value** (TRUE/FALSE) used in conditional logic. |
| **Expressions**                   | **`Arithmetic Operators`** (`Addition`, `Multiplication`), **`Function Expressions`** (`Sum`, `Cast`), `Literals` (values).                     | Components that **evaluate to a scalar value** (number, string, date).                  |
| **Catalog Objects & Identifiers** | **`Table Identifier`**, **`Column Identifier`**, `Schema Identifier`.                                                                           | References to database entities, forming the leaves of the tree when referencing data.  |

***

## Relationships Between Components

The connections (edges) in an SQL AST define the structural and hierarchical relationships between the nodes.

### 1. Structural Relationships (Composition)

These relationships define how a larger node is built from smaller, ordered components, establishing the fundamental tree structure. The relationships are often represented by properties that dictate the order and role of child nodes:

* **Statement-to-Clause:** A root `Statement` is composed of its `Clause` children. For example, a `Select Statement` is linked to its `Projection Clause` (`SELECT` list) and its `Source Clause` (`FROM`).
* **Argument Ordering:** Complex nodes like function calls or compound expressions (e.g., `(A + B) * C`) use an **ordered list of arguments** (e.g., `sql:args`) to maintain the sequence of operands, which is crucial for correct evaluation.
* **Qualified Names:** Identifiers (e.g., `Schema.Table.Column`) model their hierarchy through an ordered list of unqualified names, where the sequence determines the qualification scope.

### 2. Hierarchical Relationships (Inheritance)

These define the type system, showing that a specific component is a specialized form of a general concept. This hierarchy ensures structural integrity and allows for consistent processing.

* **Top Level:** All specific nodes are subclasses of the abstract **`Generic AST Node`**.
* **Statement Specialization:**
  * `Statement` $\rightarrow$ `Data Manipulation Statement` $\rightarrow$ **`Select Statement`**
* **Operator/Expression Type:**
  * `Operator` $\rightarrow$ `Arithmetic Operator` $\rightarrow$ **`Addition`** (`+`)
  * `Predicate` $\rightarrow$ `Comparison Operator` $\rightarrow$ **`Equals`** (`=`)

In summary, the AST structure begins with a **Statement** (the action), which is broken down into **Clauses** (the parts of the action). These clauses contain **Predicates** and **Expressions** (the logic and computations), which ultimately terminate in basic **Identifiers** or **Literals** (the data references and values). This entire structure provides a normalized, machine-readable representation of the query logic.

sources: [AST](https://ns.inria.fr/ast/sql/index.html)
