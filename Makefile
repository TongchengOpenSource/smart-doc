
release:
	mvn clean deploy -P release -DskipTests

# release with keyname
release-with-key:
	mvn clean deploy -P release -DskipTests -Dgpg.keyname=xx

checkstyle-checkstyle:
	mvn checkstyle:checkstyle

checkstyle-check:
	mvn checkstyle:check

