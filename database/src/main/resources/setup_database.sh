#!/usr/bin/env bash

db_generate_script=$1

os=$(uname)
if [[ ${os} = "Darwin" ]]; then
    script_epoch=$(stat -t '%s' "${db_generate_script}" | cut -d\" -f4)
else
    # Assume Linux with GNU date syntax
    script_epoch=$(date -r "${db_generate_script}" '+%s')
fi

db_epoch=$(mysql --defaults-file=~/oncoact.login << HERE | sed -n '2p'
SELECT UNIX_TIMESTAMP(MAX(create_time)) db_creation FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = "oncoact_test";
HERE
)

echo "[INFO] SQL database generation script epoch: ${script_epoch}"
echo "[INFO] Database epoch: ${db_epoch}"

if [[ "${db_epoch}" = "NULL" || ${script_epoch} -gt ${db_epoch} ]]; then
    echo "[INFO] Rebuilding OncoAct test database based on ${db_generate_script}"
    mysql --defaults-file=~/oncoact.login < "${db_generate_script}"
else
    echo "[INFO] Skipping OncoAct test database regeneration since database is in sync with script"
fi
