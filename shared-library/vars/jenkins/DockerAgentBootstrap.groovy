node('master') {

    stage('Checkout') {
        cleanWs()
        checkout scm
    }

    stage('Bootstrap Agent') {
        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: []]) {
            ansiColor('xterm') {
                try {
                    sh("docker run -e AGENT_URL=\"http://host.docker.internal:8080/jnlpJars/agent.jar\" --name ${params.agentID} --rm ${params.image}")
                    sh("sleep 20") // give ssh some time
                } catch(Exception e) {
                    echo "${e}"
                    currentBuild.result = 'FAILURE'
                } finally {
                }
            }
        }
    }
}
