#!/usr/bin/env sh
# @autor Edwin Betancourt <EdwinBetanc0urt@outlook.com>

# Set server values
sed -i "s|50059|$SERVER_PORT|g" all_in_one_connection.yaml
sed -i "s|WARNING|$SERVER_LOG_LEVEL|g" all_in_one_connection.yaml


# create array to iterate
SERVICES_LIST=$(echo $SERVICES_ENABLED | tr "; " "\n")

SERVICES_LIST_TO_SET=""
for SERVICE_ITEM in $SERVICES_LIST
do
    # Service to lower case
    SERVICE_LOWER_CASE=$(echo $SERVICE_ITEM | tr '[:upper:]' '[:lower:]')

    NEW_LINE="\n"
    PREFIX="        - "
    if [ -z "$SERVICES_LIST_TO_SET" ]
    then
        NEW_LINE=""
        PREFIX="- "
    fi

    # Add to the list of services
    SERVICES_LIST_TO_SET="${SERVICES_LIST_TO_SET}${NEW_LINE}${PREFIX}${SERVICE_LOWER_CASE}"
done

sed -i "s|- services_enabled|$SERVICES_LIST_TO_SET|g" all_in_one_connection.yaml


# Set data base conection values
sed -i "s|localhost|$DB_HOST|g" all_in_one_connection.yaml
sed -i "s|5432|$DB_PORT|g" all_in_one_connection.yaml
sed -i "s|adempieredb|$DB_NAME|g" all_in_one_connection.yaml
sed -i "s|adempiereuser|$DB_USER|g" all_in_one_connection.yaml
sed -i "s|adempierepass|$DB_PASSWORD|g" all_in_one_connection.yaml
sed -i "s|PostgreSQL|$DB_TYPE|g" all_in_one_connection.yaml


# Run app
./adempiere-all-in-one-server ./all_in_one_connection.yaml
