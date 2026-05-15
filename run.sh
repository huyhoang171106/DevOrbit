#!/usr/bin/env bash
set -e

cd "$(dirname "$0")/devorbit-api"

# Load .env
export $(grep -v '^#' .env | xargs)

JAR="target/devorbit-api-0.0.1-SNAPSHOT.jar"

echo "Building..."
./mvnw package -DskipTests -q

echo "Starting backend on port $SERVER_PORT ..."
nohup java -jar "$JAR" > backend.log 2>&1 &
echo "PID: $!"
echo "Logs: backend.log"
