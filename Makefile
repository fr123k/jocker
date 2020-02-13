
VERSION=$(shell docker image inspect jenkins/jenkins:lts | jq -r '.[0].ContainerConfig.Env[] | select(contains("JENKINS_VERSION"))' | cut -d'=' -f 2)
export IMAGE="fr123k/jocker:${VERSION}"

build: ## Build the jenkins in docker image.
	docker build -t $(IMAGE) -f Dockerfile .

release: build ## Push docker image to docker hub
	docker push $(IMAGE)

jocker: build ## Start the jenkins in docker container short denkins.
	docker kill jocker || echo "Ignore failure"
	echo "SEED_BRANCH='${TRAVIS_BRANCH}'"
	docker run -v /var/run/docker.sock:/var/run/docker.sock -e -d -p 8080:8080 -e SEED_BRANCH=${TRAVIS_BRANCH} --name jocker --rm ${IMAGE}

logs: ## Show the logs of the jocker container
	watch docker logs $(shell docker ps -f name=jocker -q)

test: ## Wait 60 seconds and then check the build status of the Configure job to fail if status is not SUCCESS.
	sleep 60
	docker logs $(shell docker ps -f name=jocker -q)
	@curl -s http://admin:admin@localhost:8080/job/Jenkins/job/Configure/lastBuild/consoleText
	@curl -s http://admin:admin@localhost:8080/job/Jenkins/job/Configure/lastBuild/api/json | jq -r .result | grep SUCCESS

# Absolutely awesome: http://marmelab.com/blog/2016/02/29/auto-documented-makefile.html
help: ## Print this help.
	@grep -E '^[a-zA-Z._-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
