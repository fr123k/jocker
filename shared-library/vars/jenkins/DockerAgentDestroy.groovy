
node('master') {

    stage('Checkout') {
        cleanWs()
        checkout scm
    }

    stage('Destroy Agent') {
        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: []]) {
            ansiColor('xterm') {
                try {
                    sh("docker kill ${params.agentID}")
                } catch(Exception e) {
                    echo "${e}"
                    currentBuild.result = 'FAILURE'
                }
            }
        }
    }
}
