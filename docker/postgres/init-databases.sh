#!/bin/sh
set -e

DATABASES="
inventory_db
requisition_db
temperature_db
alert_db
user_db
auth_db
audit_db
"

for database in $DATABASES; do
  exists="$(psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -tAc "SELECT 1 FROM pg_database WHERE datname = '$database'")"

  if [ "$exists" != "1" ]; then
    psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "CREATE DATABASE \"$database\" OWNER \"$POSTGRES_USER\";"
  fi
done
