API_TOKEN=$(shell docker logs $(shell docker ps -f name=jocker -q) | grep 'Api-Token:' | tr ':' '\n' | tail -n +2)

agent:
	curl http://admin:$(API_TOKEN)@localhost:8080/computer/docker-1/slave-agent.jnlp
	curl -o agent.jar http://admin:$(API_TOKEN)@localhost:8080/jnlpJars/agent.jar 
	java -jar agent.jar -jnlpUrl http://localhost:8080/computer/docker-1/slave-agent.jnlp -noreconnect -secret $(shell curl -L -s http://admin:$(API_TOKEN)@localhost:8080/computer/docker-1/slave-agent.jnlp | sed "s/.*<application-desc main-class=\"hudson.remoting.jnlp.Main\"><argument>\([a-z0-9]*\).*/\1/") -workDir "$(PWD)/remote"

agent-2:
	curl http://admin:$(API_TOKEN)@localhost:8080/computer/docker-2/slave-agent.jnlp
	curl -o agent.jar http://admin:$(API_TOKEN)@localhost:8080/jnlpJars/agent.jar 
	java -jar agent.jar -jnlpUrl http://localhost:8080/computer/docker-2/slave-agent.jnlp -noreconnect -secret $(shell curl -L -s http://admin:$(API_TOKEN)@localhost:8080/computer/docker-2/slave-agent.jnlp | sed "s/.*<application-desc main-class=\"hudson.remoting.jnlp.Main\"><argument>\([a-z0-9]*\).*/\1/") -workDir "$(PWD)/remote"
