#!/bin/bash
set -e
set -x

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

echo "Starting snapshot deployment..."
export MAVEN_OPTS="$MAVEN_OPTS -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
exec $DIR/../mvnw -B -P "blazebit-release" -s $DIR/deploy-settings.xml -DperformRelease -DskipTests -DskipNpmDeploy -Dgpg.skip=true -Dquiet=true clean deploy
