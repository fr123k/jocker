#!/bin/sh

export WORK_DIR="/home/jenkins"
export AGENT_DIR="${WORK_DIR}/bin"
export AGENT_JAR="${AGENT_DIR}/agent.jar"

mkdir -p ${AGENT_DIR}
curl -skL ${AGENT_URL} > ${AGENT_JAR}
exec java -jar ${AGENT_JAR} -workDir ${WORK_DIR}
