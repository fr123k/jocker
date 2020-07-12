node('docker') {

    stage('Checkout') {
        cleanWs()
        checkout scm
    }

    stage('Build Agent') {
        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: []]) {
            try {
                secret = jenkins.model.Jenkins.instance.getNode(params.node)?.computer?.jnlpMac.trim()
                sh("docker version")
                sh("git clone ${params.gitUrl} work")
                //TODO make this configurable
                dir("work/agents/${params.label}"){
                    sh "pwd"
                    sh("git checkout ${params.gitRevision}")
                    sh("make VERSION=latest build")
                    sh(
                    """
                    docker run -d --name ${params.node} --rm ${params.image} -noreconnect -url ${env.JENKINS_MASTER} ${secret} ${params.node}
                    """
                    )
                }
            } catch(Exception e) {
                echo "${e}"
                currentBuild.result = 'FAILURE'
            } finally {
            }
        }
    }
}
