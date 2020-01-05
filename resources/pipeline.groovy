node('master') {
    stage('Checkout') {
        cleanWs()
        checkout scm
    }

    stage('Configuration') {
        // set config files in master
        sh('rsync -r ${HOME}/workspace/Jenkins/Configure/resources/config/configuration-as-code-plugin/ ${HOME}/casc-config/')

        // add programmacticly needed script approvals
        load('resources/config/groovy/scriptApproval.groovy')

        // run configuration from config file
        load('resources/config/groovy/triggerConfigurationAsCodePlugin.groovy')

        // set public key for bootstrapping user
        load('resources/config/groovy/userPublicKeys.groovy')

        // set the timezone
        load('resources/config/groovy/timezone.groovy')

        // disable csrf for easier jenkins api calls
        load('resources/config/groovy/csrf.groovy')
    }

    stage('Seed') {
        // https://issues.jenkins-ci.org/browse/JENKINS-44142
        // --> Note: when using multiple Job DSL build steps in a single job, set this to "Delete" only for the last Job DSL build step. 
        // Otherwise views may be deleted and re-created. See JENKINS-44142 for details.
        jobDsl(targets: 'resources/jobDSL/folders.groovy', sandbox: false, removedJobAction: 'IGNORE')
        jobDsl(targets: 'resources/jobDSL/*.groovy', sandbox: false, removedJobAction: 'DELETE')
    }
}
