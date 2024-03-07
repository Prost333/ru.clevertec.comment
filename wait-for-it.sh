#!/bin/bash
set -e

until echo > /dev/tcp/db/5432; do
  echo "Postgres is unavailable - sleeping"
  sleep 1
done

echo "Postgres is up - executing command"
exec java -jar app.jar