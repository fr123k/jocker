
VERSION=$(shell docker image inspect jenkins/jenkins:lts | jq -r '.[0].Config.Env[] | select(contains("JENKINS_VERSION"))' | cut -d'=' -f 2)
export NAME=fr123k/jocker
export IMAGE="${NAME}:${VERSION}"
export LATEST="${NAME}:latest"
export ADMIN_PASSWORD=$(shell pwgen -s 16 1)

SEED_BRANCH=$(shell [ -z "${TRAVIS_PULL_REQUEST_BRANCH}" ] && echo "${TRAVIS_BRANCH}"|| echo "${TRAVIS_PULL_REQUEST_BRANCH}")
API_TOKEN=$(shell docker logs $(shell docker ps -f name=jocker -q) | grep 'Api-Token:' | tr ':' '\n' | tail -n +2)

ENV_SEED_BRANCH_CONFIGURE=$(shell [ -z "${SEED_BRANCH}" ] && echo ""|| echo "-e SEED_BRANCH_CONFIGURE=${SEED_BRANCH}")

ENV_SEED_BRANCH_JOBS=$(shell [ -z "${SEED_BRANCH}" ] && echo ""|| echo "-e SEED_BRANCH_JOBS=${SEED_BRANCH}")

DOCKER_HOST=$(shell ip -4 addr show docker0 | grep -Po 'inet \K[\d.]+')
ENV_FILE=$(shell [ -f "./jocker.env" ] && echo "--env-file ./jocker.env"|| echo "")

pull-base:  ## Push docker image to docker hub
	docker pull jenkins/jenkins:lts

build: ## Build the jenkins in docker image.
	docker build -t $(IMAGE) -f Dockerfile .

release: build ## Push docker image to docker hub
	docker tag ${IMAGE} ${LATEST}
	docker push ${NAME}

start: ## Start the jenkins in docker container short jocker.
	docker kill jocker || echo "Ignore failure"
	echo "SEED_BRANCH='${SEED_BRANCH}'"
	docker run -d --memory 3g -p 50000:50000 -p 8080:8080 -e ADMIN_PASSWORD="${ADMIN_PASSWORD}" ${ENV_SEED_BRANCH_JOBS} ${ENV_FILE} --name jocker --rm ${IMAGE}

jocker: build start ## Start the jenkins in docker container short jocker.

local: build ## Start the jenkins in docker container short jocker.
	docker kill jocker || echo "Ignore failure"
	echo "SEED_BRANCH='${SEED_BRANCH}'"
	docker run -d -p 50000:50000 -p 8080:8080 ${ENV_SEED_BRANCH_JOBS} ${ENV_FILE} --name jocker --rm ${IMAGE}

logs: ## Show the logs of the jocker container
	docker logs -f $(shell docker ps -f name=jocker -q)

test: ## check the build status of the Configure job to fail if status is not SUCCESS.
	docker logs $(shell docker ps -f name=jocker -q)
	./scripts/jenkins-wait.sh jenkins/job/Setup
	@curl -s http://admin:$(API_TOKEN)@localhost:8080/job/jenkins/job/SharedLib/lastBuild/consoleText
	@curl -s http://admin:$(API_TOKEN)@localhost:8080/job/jenkins/job/Configure/lastBuild/consoleText
	@curl -s http://admin:$(API_TOKEN)@localhost:8080/job/jenkins/job/Jobs/lastBuild/consoleText
	@curl -s http://admin:$(API_TOKEN)@localhost:8080/job/jenkins/job/Setup/lastBuild/consoleText
	@curl -s http://admin:$(API_TOKEN)@localhost:8080/job/jenkins/job/Setup/lastBuild/api/json | jq -r .result | grep SUCCESS

# Absolutely awesome: http://marmelab.com/blog/2016/02/29/auto-documented-makefile.html
help: ## Print this help.
	@grep -E '^[a-zA-Z._-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

git-server:
	docker run -p 22:22 -it -v $(PWD)/../:/git-server -e REPOSITORY=fr123k --name github --rm github-server-docker
