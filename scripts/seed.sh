#!/usr/bin/env bash
set -euo pipefail

DB_NAME="matchme"
DB_USER="postgres"

psql -U "$DB_USER" -d "$DB_NAME" -f "$(dirname "$0")/seed.sql"

echo "Seeded 100 users into $DB_NAME"
