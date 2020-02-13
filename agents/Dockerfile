FROM alpine:latest

USER root
RUN apk update && apk upgrade && \
    apk add curl && \
    apk --no-cache add openjdk11 --repository=http://dl-cdn.alpinelinux.org/alpine/edge/community

# adduser --uid 2000 --gid 2000 

RUN addgroup -S jenkins && adduser -S jenkins -G jenkins

COPY --chown=jenkins start-agent.sh /bin/agent

USER jenkins
