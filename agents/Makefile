
VERSION=$(shell docker image inspect jenkins/jenkins:lts | jq -r '.[0].ContainerConfig.Env[] | select(contains("JENKINS_VERSION"))' | cut -d'=' -f 2)
export IMAGE="fr123k/jocker-agent:${VERSION}"

build: ## Build the jenkins in docker image.
	docker build -t $(IMAGE) -f Dockerfile .

release: build ## Push docker image to docker hub
	docker push $(IMAGE)

test: build 
	docker run -e AGENT_URL="http://host.docker.internal:8080/jnlpJars/agent.jar" $(IMAGE)

# Absolutely awesome: http://marmelab.com/blog/2016/02/29/auto-documented-makefile.html
help: ## Print this help.
	@grep -E '^[a-zA-Z._-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
