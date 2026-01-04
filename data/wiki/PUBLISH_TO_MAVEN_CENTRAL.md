**Publish to Maven Central**

Set the repository secrets in *Settings → Secrets and variables → Actions*:

- `OSSRH_USERNAME`
- `OSSRH_PASSWORD`
- `GPG_PRIVATE_KEY` (ASCII-armored) (use data/scripts/generate-gpg-secrets.sh)
- `GPG_PASSPHRASE` (if applicable) (see data/scripts/generate-gpg-secrets.sh)

Optional: run a build/verify in CI before creating the release tag.

**Artifacts published by the release**

The release publishes these modules and attached artifacts:

- `jdsql-api`: main JAR, `-sources.jar`, `-javadoc.jar`, module POM, and GPG signature (`.asc`).
- `jdsql-spi`: main JAR, `-sources.jar`, `-javadoc.jar`, module POM, and GPG signature (`.asc`).
- `test-support`: main JAR, `-sources.jar`, `-javadoc.jar`, module POM, and GPG signature (`.asc`).
- `plugins/jdsql-mysql`: main JAR, `-sources.jar`, `-javadoc.jar`, module POM, and GPG signature (`.asc`).
- `plugins/jdsql-postgresql`: main JAR, `-sources.jar`, `-javadoc.jar`, module POM, and GPG signature (`.asc`).

Note: `jdsql-core` is built locally but has `<maven.deploy.skip>true</maven.deploy.skip>` and is not published to OSSRH.

**Why `packaging = pom` modules publish only a POM**

Modules with `packaging = pom` (for example the root project and the `plugins` parent) do not produce runtime JARs. Maven publishes their POM files only. These parent/aggregator POMs carry `dependencyManagement`, `pluginManagement` and module lists — publishing the POM exposes that metadata to consumers but there is no `-<version>.jar` for such modules.

You can verify this locally:

```bash
# Build everything that would be attached by the release (no deploy)
./mvnw -P release -DskipTests clean package

# Or build and install to the local repo to verify consumer resolution
./mvnw -P release -DskipTests clean install

# Check module target artifacts (replace '1.0' with the actual version if different)
ls -l jdsql-api/target/jdsql-api-*.jar jdsql-api/target/jdsql-api-*-sources.jar jdsql-api/target/jdsql-api-*-javadoc.jar
ls -l jdsql-spi/target/jdsql-spi-*.jar jdsql-spi/target/jdsql-spi-*-sources.jar jdsql-spi/target/jdsql-spi-*-javadoc.jar
ls -l test-support/target/test-support-*.jar
ls -l plugins/jdsql-mysql/target/jdsql-mysql-*.jar plugins/jdsql-postgresql/target/jdsql-postgresql-*.jar

# After 'install' verify local repository coordinates
ls -l ~/.m2/repository/lan/tlab/jdsql-api/* ~/.m2/repository/lan/tlab/jdsql-spi/*

# Verify packaging=pom modules produce a POM and no JAR
ls -l plugins/target/plugins-*.pom
test -f plugins/target/plugins-1.0.pom && echo "plugins POM present" || echo "plugins POM missing"
test -f plugins/target/plugins-1.0.jar && echo "unexpected JAR for plugins" || echo "no JAR for plugins (expected)"
```

**Notes**

- The CI release workflow config in `.github/workflows/release.yml` runs `mvn -B -DskipTests clean deploy -P release` and uses `actions/setup-java` to configure OSSRH credentials and GPG signing.
- Source and Javadoc jars are attached via the source/javadoc plugins configured in the parent POM. GPG signatures (`.asc`) are produced during the verify phase when signing is enabled (CI supplies the GPG key).

If you want, I can also add a short checklist with exact expected file paths including the concrete version numbers, or add an aggregate `-pl` command that builds only the publishable modules.
