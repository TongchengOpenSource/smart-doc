
release:
	mvn clean deploy -P release -DskipTests

# release with keyname
release-with-key:
	mvn clean deploy -P release -DskipTests -Dgpg.keyname=xx
