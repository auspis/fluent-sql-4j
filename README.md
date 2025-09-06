# wiki

* [monorepo](./data/WIKI/MONOREPO/README.md)
* [deploy](./data/WIKI/DEPLOY/README.md)

## format code

```bash
./mvnw spotless:apply
```

**Note**: the **git-build-hook-maven-plugin** is configured to automatically install a hook to format the code as soon as the `validate` phase is invoked (eg `./mvnw verify -DskipTests`).

## run tests

* the **maven-surefire-plugin** is configured to:
  * skip integration tests
  * skip unit tests if the property `skip.unit.tests` is `true`
* the **maven-failsafe-plugin** is configured to run only integration tests
* the **integration tests** are those whose file name ends in **IT.java**

### run only unit tests

```bash
./mvnw -am -pl :sqlbuilder test
```

```bash
./mvnw -am -pl :sqlbuilder package
```

```bash
./mvnw -am -pl :sqlbuilder install
```

### run only integration tests

```bash
./mvnw -am -pl :sqlbuilder verify -Dskip.unit.tests=true
```

or

```bash
./mvnw -am -pl :sqlbuilder integration-test -Dskip.unit.tests=true
```

### run unit and integration tests

```bash
./mvnw -am -pl :sqlbuilder verify
```

```bash
./mvnw -am -pl :sqlbuilder integration-test
```

