node('docker') {

    stage('Checkout') {
        cleanWs()
        checkout scm
    }

    stage('Bootstrap Agent') {
        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: []]) {
            try {
                secret = jenkins.model.Jenkins.instance.getNode(params.node)?.computer?.jnlpMac.trim()
                sh("docker version")
                sh(
                    """
                    docker run -d --name ${params.node} --memory-swap=-1 --rm ${params.image} -noreconnect -url ${env.JENKINS_MASTER} ${secret} ${params.node}
                    """
                )
            } catch(Exception e) {
                echo "${e}"
                currentBuild.result = 'FAILURE'
            } finally {
            }
        }
    }
}
