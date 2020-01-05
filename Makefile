export IMAGE="jocker"

build: ## Build the jenkins in docker image.
	docker build -t $(IMAGE) -f Dockerfile .

jocker: build ## Start the jenkins in docker container short denkins.
	docker kill jocker || echo "Ignore failure"
	docker run -d -p 8080:8080 --name jocker --rm ${IMAGE}

logs: ## Show the logs of the jocker container
	watch docker logs $(shell docker ps -f ancestor=jocker -q)

test:
	sleep 60
	docker logs $(shell docker ps -f ancestor=jocker -q)
	@curl -s http://localhost:8080/job/Jenkins/job/Configure/lastBuild/api/json | jq -r .result | grep SUCCESS

# Absolutely awesome: http://marmelab.com/blog/2016/02/29/auto-documented-makefile.html
help: ## Print this help.
	@grep -E '^[a-zA-Z._-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
