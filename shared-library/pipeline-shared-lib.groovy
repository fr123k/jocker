node('master') {
    stage('Checkout') {
        cleanWs()
        checkout scm
    }

    stage('Configuration') {
        // run configuration from config file
        load('shared-library/config/groovy/sharedLibrary.groovy')
    }
}
