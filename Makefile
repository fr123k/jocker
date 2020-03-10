
VERSION=$(shell docker image inspect jenkins/jenkins:lts | jq -r '.[0].ContainerConfig.Env[] | select(contains("JENKINS_VERSION"))' | cut -d'=' -f 2)
export NAME=fr123k/jocker
export IMAGE="${NAME}:${VERSION}"
export LATEST="${NAME}:latest"
export ADMIN_PASSWORD=$(shell pwgen -s 16 1)

SEED_BRANCH=$(shell [ -z "${TRAVIS_PULL_REQUEST_BRANCH}" ] && echo "${TRAVIS_BRANCH}"|| echo "${TRAVIS_PULL_REQUEST_BRANCH}")
API_TOKEN=$(shell docker logs $(shell docker ps -f name=jocker -q) | grep 'Api-Token:' | tr ':' '\n' | tail -n +2)

DOCKER_HOST=$(shell ip -4 addr show docker0 | grep -Po 'inet \K[\d.]+')

pull-base:  ## Push docker image to docker hub
	docker pull jenkins/jenkins:lts

build: ## Build the jenkins in docker image.
	docker build -t $(IMAGE) -f Dockerfile .

release: build ## Push docker image to docker hub
	docker tag ${IMAGE} ${LATEST}
	docker push ${NAME}

jocker: build ## Start the jenkins in docker container short denkins.
	docker kill jocker || echo "Ignore failure"
	echo "SEED_BRANCH='${SEED_BRANCH}'"
	docker run -d -p 50000:50000 -p 8080:8080 -e ADMIN_PASSWORD="${ADMIN_PASSWORD}" -e SEED_BRANCH_CONFIGURE=${SEED_BRANCH} -e SEED_BRANCH_JOBS=${SEED_BRANCH} --name jocker --rm ${IMAGE}

local: build ## Start the jenkins in docker container short denkins.
	docker kill jocker || echo "Ignore failure"
	echo "SEED_BRANCH='${SEED_BRANCH}'"
	docker run -d -p 50000:50000 -p 8080:8080 -e SEED_BRANCH_CONFIGURE=${SEED_BRANCH} -e SEED_BRANCH_JOBS=${SEED_BRANCH} --name jocker --rm ${IMAGE}

logs: ## Show the logs of the jocker container
	docker logs -f $(shell docker ps -f name=jocker -q)

test: ## check the build status of the Configure job to fail if status is not SUCCESS.
	docker logs $(shell docker ps -f name=jocker -q)
	./scripts/jenkins-wait.sh Jenkins/job/Setup
	@curl -s http://admin:$(API_TOKEN)@localhost:8080/job/Jenkins/job/Configure/lastBuild/consoleText
	@curl -s http://admin:$(API_TOKEN)@localhost:8080/job/Jenkins/job/Jobs/lastBuild/consoleText
	@curl -s http://admin:$(API_TOKEN)@localhost:8080/job/Jenkins/job/Setup/lastBuild/consoleText
	@curl -s http://admin:$(API_TOKEN)@localhost:8080/job/Jenkins/job/Setup/lastBuild/api/json | jq -r .result | grep SUCCESS

test-agent-pulumi:
	docker logs $(shell docker ps -f name=agent -q)
	./scripts/jenkins-cli.sh pulumi $(API_TOKEN)
	@curl -s http://admin:$(API_TOKEN)@localhost:8080/job/pulumi/lastBuild/api/json | jq -r .result | grep SUCCESS

agent: ## start the jocker golang pulumi agent and join the jenkins master
	docker pull fr123k/jocker-agents-golang
	docker run -d --name agent --rm fr123k/jocker-agents-golang -url http://$(DOCKER_HOST):8080 $(shell curl -L -s http://admin:$(API_TOKEN)@localhost:8080/computer/docker-1/slave-agent.jnlp | sed "s/.*<application-desc main-class=\"hudson.remoting.jnlp.Main\"><argument>\([a-z0-9]*\).*/\1/") docker-1
	sleep 10
	docker ps
	docker logs $(shell docker ps -f name=agent -q)

agent-logs: ## Show the logs of the jocker container
	docker logs -f $(shell docker ps -f name=agent -q)

# Absolutely awesome: http://marmelab.com/blog/2016/02/29/auto-documented-makefile.html
help: ## Print this help.
	@grep -E '^[a-zA-Z._-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
