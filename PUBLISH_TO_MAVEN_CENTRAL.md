
    Imposta i secrets nel repo (Settings → Secrets and variables → Actions):
        OSSRH_USERNAME
        OSSRH_PASSWORD
        GPG_PRIVATE_KEY (ASCII-armored)
        GPG_PASSPHRASE (se applicabile)
    (Optional) Triggera un build/verify in CI prima di fare il tag di rilascio:
        Local checks:
            mvn -pl test-support -DskipTests clean package → verifica test-support-<ver>-tests.jar
            mvn -pl :jdsql-core -am -DskipITs test-compile → verifica che consumer risolvano test-jar
