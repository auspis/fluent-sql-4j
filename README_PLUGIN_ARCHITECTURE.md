# Plugin Architecture Implementation Guide

## Overview

This directory contains comprehensive documentation for implementing a plugin-based architecture for SQL dialects in the r4j framework. The goal is to make the framework extensible, allowing the community to contribute new database dialect implementations while maintaining 100% backward compatibility with existing code.

## 📚 Documentation Files

### 🎯 [QUICK_REFERENCE.md](QUICK_REFERENCE.md) - **START HERE**

Quick navigation guide and common questions. Best starting point for understanding the project.

### 📋 [PLUGIN_ARCHITECTURE_ISSUES.md](PLUGIN_ARCHITECTURE_ISSUES.md)

Detailed breakdown of **18 GitHub issues** organized in **8 phases**. Each issue includes:
- Detailed description and motivation
- Acceptance criteria
- Technical notes and implementation guidance
- Dependencies and effort estimates
- Implementation order recommendations

### 📝 [GITHUB_ISSUES_TEMPLATE.md](GITHUB_ISSUES_TEMPLATE.md)

Ready-to-copy templates for creating GitHub issues. Includes:
- Full issue descriptions for Issues #1-9
- Labels, milestones, assignees
- Code examples and test scenarios
- Instructions for using the templates

### 🏗️ [ARCHITECTURE_OVERVIEW.md](ARCHITECTURE_OVERVIEW.md)

Visual and conceptual overview of the architecture. Contains:
- Architecture diagrams
- Component descriptions
- Data flow examples
- Package structure
- ServiceLoader (SPI) explanation
- Migration path and benefits

## 🚀 Quick Start

### For Project Managers

1. Read [QUICK_REFERENCE.md](QUICK_REFERENCE.md) for overview
2. Review [PLUGIN_ARCHITECTURE_ISSUES.md](PLUGIN_ARCHITECTURE_ISSUES.md) for scope and estimates
3. Use [GITHUB_ISSUES_TEMPLATE.md](GITHUB_ISSUES_TEMPLATE.md) to create issues in GitHub
4. Assign issues according to the recommended phases

### For Developers

1. Read [QUICK_REFERENCE.md](QUICK_REFERENCE.md) for overview
2. Review [ARCHITECTURE_OVERVIEW.md](ARCHITECTURE_OVERVIEW.md) to understand the design
3. Pick an issue from [PLUGIN_ARCHITECTURE_ISSUES.md](PLUGIN_ARCHITECTURE_ISSUES.md)
4. Implement following the acceptance criteria
5. Run `./mvnw spotless:apply` before committing (mandatory!)

### For Architects

1. Read [ARCHITECTURE_OVERVIEW.md](ARCHITECTURE_OVERVIEW.md) for design details
2. Review key technical decisions in [QUICK_REFERENCE.md](QUICK_REFERENCE.md)
3. Consider [PLUGIN_ARCHITECTURE_ISSUES.md](PLUGIN_ARCHITECTURE_ISSUES.md) Phase 7 for advanced features

## 📊 Project Statistics

- **Total Issues:** 18
- **Total Estimated Effort:** 50-65 hours
- **Minimum Viable Product:** 10 issues (Issues 1-10, 13)
- **Complete Initial Release:** 14 issues (Issues 1-14)
- **Enhanced Release:** All 18 issues

## 🎯 Implementation Phases

|  Phase  | Issues | Effort |                Description                 |
|---------|--------|--------|--------------------------------------------|
| Phase 1 | 1-2    | 4-6h   | Core infrastructure (interface, registry)  |
| Phase 2 | 3-6, 8 | 10-13h | Built-in plugins (MySQL, PostgreSQL, etc.) |
| Phase 3 | 9-10   | 6-8h   | DSL integration and refactoring            |
| Phase 4 | 11-12  | 6-7h   | Documentation and examples                 |
| Phase 5 | 13-14  | 9-12h  | Integration and migration tests            |
| Phase 6 | 15     | 3-4h   | Performance optimization                   |
| Phase 7 | 16-18  | 17-23h | Advanced features (optional)               |

## 🏗️ Core Components

### SqlDialectPlugin Interface

```java
public interface SqlDialectPlugin {
    String getDialectName();
    String getVersion();
    SqlRenderer createRenderer();
    boolean supports(String dialectName);
    Set<String> getSupportedFeatures();
}
```

### SqlDialectRegistry

Central registry using Java ServiceLoader (SPI) for automatic plugin discovery.

### Built-in Plugins

- **StandardSQLDialectPlugin** - SQL:2008 standard
- **MySQLDialectPlugin** - MySQL and MariaDB
- **PostgreSQLDialectPlugin** - PostgreSQL
- **SqlServerDialectPlugin** - Microsoft SQL Server

### DSL Class (Refactored)

New instance-based API while maintaining all existing static methods:

```java
// New: dialect-specific
DSL mysql = DSL.mysql();
mysql.select("name").from("users").build();

// Old: still works (backward compatible)
DSL.select("name").from("users").build();
```

## ✅ Key Features

1. **🔌 Extensibility:** Community can create plugins for any database
2. **🔍 Auto-Discovery:** ServiceLoader finds plugins automatically
3. **📦 Modularity:** Each dialect can be a separate JAR
4. **🔄 Backward Compatible:** Existing code works unchanged
5. **🔒 Type Safety:** Compile-time checking via interface
6. **🧪 Testability:** Easy to mock plugins
7. **⚡ Performance:** Cached registry, no runtime overhead

## 🎓 Usage Examples

### Basic Usage (New API)

```java
// Create dialect-specific DSL
DSL mysql = DSL.mysql();
DSL postgres = DSL.postgresql();

// Use with fluent API
String mysqlSql = mysql.select("name", "email")
    .from("users")
    .where("active").eq(true)
    .build();

String postgresSql = postgres.select("name", "email")
    .from("users")
    .where("active").eq(true)
    .build();
```

### Dynamic Dialect Selection

```java
String dialect = config.getDatabaseType();
DSL dsl = DSL.forDialect(dialect);
String sql = dsl.select("name").from("users").build();
```

### Query Supported Dialects

```java
Set<String> dialects = DSL.getSupportedDialects();
// Returns: [sql2008, mysql, postgresql, sqlserver]
```

### Create External Plugin

```java
// 1. Implement interface
public class OracleDialectPlugin implements SqlDialectPlugin {
    // ... implementation
}

// 2. Create META-INF/services file
// 3. Package as JAR
// 4. Add to classpath
// 5. Plugin is auto-discovered!

DSL oracle = DSL.forDialect("oracle");
```

## 📋 Implementation Checklist

### Minimum Viable Product (MVP)

- [ ] Phase 1: Core infrastructure (Issues 1-2)
- [ ] Phase 2: Built-in plugins (Issues 3-6, 8)
- [ ] Phase 3: DSL integration (Issues 9-10)
- [ ] Phase 5: Basic testing (Issue 13)

### Complete Initial Release

- [ ] All MVP items
- [ ] Phase 4: Documentation (Issues 11-12)
- [ ] Phase 5: Full testing (Issues 13-14)

### Enhanced Release (Optional)

- [ ] All initial release items
- [ ] Phase 6: Optimization (Issue 15)
- [ ] Phase 7: Advanced features (Issues 16-18)

## 🔧 Development Commands

```bash
# Set Java 21
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Format code (REQUIRED before commit!)
./mvnw spotless:apply

# Build
./mvnw clean compile

# Run tests
./mvnw test

# Run integration tests
./mvnw verify

# Test specific module
./mvnw test -pl sql
```

## 📖 Documentation Structure

```
r4j/
├── QUICK_REFERENCE.md           # Navigation and quick facts
├── PLUGIN_ARCHITECTURE_ISSUES.md # Detailed issue breakdown
├── GITHUB_ISSUES_TEMPLATE.md    # Copy-paste issue templates
├── ARCHITECTURE_OVERVIEW.md     # Design and architecture
└── README_PLUGIN_ARCHITECTURE.md # This file
```

## 🎯 Success Criteria

### MVP Success Criteria

- ✅ Plugin system works with all built-in dialects
- ✅ Backward compatibility: all existing tests pass unchanged
- ✅ Auto-discovery via ServiceLoader works
- ✅ Can use DSL.mysql(), DSL.postgresql(), etc.
- ✅ Can create external plugin with example

### Complete Release Success Criteria

- ✅ All MVP criteria
- ✅ Comprehensive documentation created
- ✅ Migration guide available
- ✅ Example external plugin works
- ✅ Integration tests pass
- ✅ No performance regression

## 🚦 Getting Started

### Step 1: Understand the Architecture

Read [ARCHITECTURE_OVERVIEW.md](ARCHITECTURE_OVERVIEW.md) to understand the design.

### Step 2: Review the Issues

Read [PLUGIN_ARCHITECTURE_ISSUES.md](PLUGIN_ARCHITECTURE_ISSUES.md) to see all work items.

### Step 3: Create GitHub Issues

Use templates from [GITHUB_ISSUES_TEMPLATE.md](GITHUB_ISSUES_TEMPLATE.md) to create issues.

### Step 4: Start Implementation

Follow the recommended order:
1. **Phase 1:** Core infrastructure (foundation)
2. **Phase 2:** Built-in plugins (functionality)
3. **Phase 3:** DSL integration (user API)
4. **Phase 4-5:** Documentation and testing
5. **Phase 6-7:** Optimization and advanced features (optional)

### Step 5: Test Thoroughly

- Run existing tests (should pass unchanged)
- Create new tests for each feature
- Use Testcontainers for integration tests
- Verify backward compatibility

## ❓ Common Questions

See [QUICK_REFERENCE.md](QUICK_REFERENCE.md) for answers to common questions including:
- Do I need to modify existing code?
- How do I create an external plugin?
- Can I use multiple dialects in the same app?
- What about performance?
- How do I test my plugin?

## 🤝 Contributing

When implementing features:
1. **Follow coding standards** (no `var`, Java 21, immutable structures)
2. **Format before commit** (`./mvnw spotless:apply` - mandatory!)
3. **Test thoroughly** (unit + integration tests)
4. **Document changes** (JavaDoc, README updates)
5. **Maintain backward compatibility** (critical requirement)

## 📞 Support

For questions:
1. Check [QUICK_REFERENCE.md](QUICK_REFERENCE.md) Common Questions
2. Review relevant documentation file
3. Consult specific issue in [PLUGIN_ARCHITECTURE_ISSUES.md](PLUGIN_ARCHITECTURE_ISSUES.md)
4. Check [ARCHITECTURE_OVERVIEW.md](ARCHITECTURE_OVERVIEW.md) for design decisions

## 🎉 Benefits

Once implemented, this architecture will provide:

- **For Users:**
  - Easy multi-database support
  - Clean, dialect-specific APIs
  - Backward compatible (no code changes needed)
- **For Contributors:**
  - Clear extension points
  - Well-defined interfaces
  - Auto-discovery mechanism
  - Comprehensive examples
- **For Maintainers:**
  - Modular, testable code
  - Separate dialect concerns
  - Easy to add new dialects
  - Community contributions enabled

## 📈 Roadmap

### Current: Documentation Phase

✅ Comprehensive documentation created  
✅ Issues defined and organized  
✅ Implementation plan established

### Next: MVP Implementation

- [ ] Implement core infrastructure
- [ ] Create built-in plugins
- [ ] Refactor DSL class
- [ ] Verify backward compatibility

### Future: Enhancement Phase

- [ ] Performance optimization
- [ ] Advanced features
- [ ] Multi-module distribution
- [ ] Community plugin ecosystem

---

**Remember:** The goal is incremental, safe refactoring that adds powerful extensibility while maintaining 100% backward compatibility. Take it one issue at a time, test thoroughly, and document as you go.

Happy coding! 🚀
