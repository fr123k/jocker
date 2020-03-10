#!/bin/bash -x

MAX_ATTEMPS=20
set +x
API_TOKEN=$(docker logs $(docker ps -f name=jocker -q) | grep 'Api-Token:' | tr ':' '\n' | tail -n +2 | tr -d '\n\r')
set -x
while [ -z "$API_TOKEN" ]
do
    set +x
    API_TOKEN=$(docker logs $(docker ps -f name=jocker -q) | grep 'Api-Token:' | tr ':' '\n' | tail -n +2 | tr -d '\n\r')
    set -x
    echo "Api-Token $API_TOKEN"
    ((BOOTSTRAP_ATTEMPS++))
    echo "Wait for jenkins bootstrap to finish tries=$BOOTSTRAP_ATTEMPS."
    sleep 10
    if [[ $BOOTSTRAP_ATTEMPS = $MAX_ATTEMPS ]]; then
        echo "Reached MAX_ATTEMPS $MAX_ATTEMPS==$BOOTSTRAP_ATTEMPS."
        break
    fi
done

JOB_URL="http://admin:${API_TOKEN}@localhost:8080/job/$1"
JOB_STATUS_URL=${JOB_URL}/lastBuild/api/json
JOB_CONSOLE_URL=${JOB_URL}/lastBuild/consoleText

GREP_RETURN_CODE=0

# Poll every 7seconds until the build is finished
while [ $GREP_RETURN_CODE -eq 0 ]
do
    if [[ $ATTEMPS = $MAX_ATTEMPS ]]; then
        echo "Reached MAX_ATTEMPS $MAX_ATTEMPS==$ATTEMPS."
        exit 1
    fi
    ((ATTEMPS++))
    echo "Wait for jenkins job $1 to finish tries=$ATTEMPS."
    sleep 5
    # Check if jenkins build can be fetched
    STATUSCODE=$(curl -s --output /dev/null --write-out "%{http_code}" $JOB_STATUS_URL) 
    if [[ $STATUSCODE -ne 200 ]]; then
        # non status code 200 means authentication failed so retry
        continue
    fi
    # Grep will return 0 while the build is running:
    curl -s $JOB_STATUS_URL | grep result\":null > /dev/null
    #|| if [ "$?" == "1" ];then
    GREP_RETURN_CODE=$?
done
