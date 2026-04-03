#!/usr/bin/env bash
set -euo pipefail

DB_NAME="matchme"
DB_USER="postgres"

# Drop and recreate DB
psql -U "$DB_USER" -c "DROP DATABASE IF EXISTS $DB_NAME;"
psql -U "$DB_USER" -c "CREATE DATABASE $DB_NAME;"

echo "Database reset: $DB_NAME"
