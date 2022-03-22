
node('docker') {

    stage('Checkout') {
        cleanWs()
        checkout scm
    }

    stage('Destroy Agent') {
        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: []]) {
            try {
                sh("docker ps")
                sh("docker stop ${params.node}")
                sh("docker ps")
            } catch(Exception e) {
                echo "${e}"
                currentBuild.result = 'FAILURE'
            }
        }
    }
}
