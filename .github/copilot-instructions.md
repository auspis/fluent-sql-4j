# Copilot Repository Instructions

## Overview

This repository is a multi-module Java project managed with Maven. It is structured to support modular development and integration testing, and is intended for use with Java 21.

## Java Version

- Java 21 is required for all modules.

## Build Tool

- Maven (multi-module project)
- Each module contains its own `pom.xml` and is managed from the root `pom.xml`.

## Project Structure

- Root directory contains:
  - `pom.xml` (parent POM)
  - Subfolders for each module (e.g., `sqlbuilder/`, `test-integration/`, etc.)
  - `.github/` for GitHub-specific configuration
- Common modules:
  - `sqlbuilder/`: Core SQL builder logic
  - `test-integration/`: Integration tests, uses Testcontainers
  - Other modules for shared code, applications, and services

## Conventions

- Source code in `src/main/java/`
- Tests in `src/test/java/`
- Use AssertJ for assertions in tests
- Use JUnit 5 for unit and integration tests
- Integration tests may use Testcontainers for database emulation
- SQL code is generated using the project's SQL builder classes

## Dependencies

- Testcontainers for integration testing
- AssertJ for fluent assertions
- JUnit 5 for testing
- MySQL (via Testcontainers) for integration tests

## Coding Guidelines

- Follow standard Java 21 conventions
- Use builder patterns for constructing SQL ASTs when there are more than two fields, otherwise an all args constructor is ok
- Keep modules decoupled and reusable
- Prefer immutable data structures where possible

## How to Add a Module

1. Create a new directory at the root
2. Add a `pom.xml` for the module
3. Register the module in the root `pom.xml` under `<modules>`

## How to Run Tests

- make sure you are in the root folder or `cd` to it
- Use `./mvnw clean test` at the root to run unit tests
- Use `./mvnw clean verify` at the root to run all tests (unit and integration)
- The project is a multi module maven project, so in some cases you may need to add -am to compile dependencies
- When you need to run integration tests try to run only the needed ones
- Integration tests are located in `test-integration/`

## Additional Notes

- The repository may contain scripts in `data/scripts/` for development automation
- Use the provided Maven Wrapper (`mvnw`) for consistent builds
- For any new code, ensure it is covered by tests and follows the project structure

## Contact

For questions or contributions, refer to the `README.md` or contact the maintainers listed there.
