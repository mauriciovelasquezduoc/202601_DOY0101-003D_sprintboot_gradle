#!/usr/bin/env sh
# Gradle wrapper script
DIR="$(cd "$(dirname "$0")"; pwd -P)"
if [ -f "$DIR/gradle/wrapper/gradle-wrapper.jar" ]; then
  exec java -jar "$DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
else
  exec gradle "$@"
fi
