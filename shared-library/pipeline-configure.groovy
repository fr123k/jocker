node('master') {
    stage('Checkout') {
        cleanWs()
        checkout scm
    }

    stage('Configuration') {
        // run SetupWizard from fr123k/jenkins-shared-library
        def setup = load('shared-library/config/groovy/setup.groovy')
        setup()
            .getScriptApproval()
                .approve('method jenkins.model.Jenkins getNode java.lang.String')
                .approve('method hudson.model.Slave getComputer')
                .approve('method hudson.slaves.SlaveComputer getJnlpMac')
    }
}
