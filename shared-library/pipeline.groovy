node('master') {
    stage('Checkout') {
        cleanWs()
        checkout scm
    }

    stage('Configuration') {
        // set config files in master
        sh('rsync -r ${HOME}/workspace/Jenkins/Configure/shared-library/config/casc-config/ ${HOME}/casc-config/')

        // add programmacticly needed script approvals
        load('shared-library/config/groovy/scriptApproval.groovy')

        // run configuration from config file
        load('shared-library/config/groovy/cascPlugin.groovy')

        // set public key for bootstrapping user
        load('shared-library/config/groovy/userPublicKeys.groovy')

        // set the timezone
        load('shared-library/config/groovy/timezone.groovy')

        // disable csrf for easier jenkins api calls
        load('shared-library/config/groovy/csrf.groovy')
    }

    stage('Seed') {
        // https://issues.jenkins-ci.org/browse/JENKINS-44142
        // --> Note: when using multiple Job DSL build steps in a single job, set this to "Delete" only for the last Job DSL build step. 
        // Otherwise views may be deleted and re-created. See JENKINS-44142 for details.
        jobDsl(targets: 'shared-library/jobDSL/folders.groovy', sandbox: false, removedJobAction: 'IGNORE')
        jobDsl(targets: 'shared-library/jobDSL/*.groovy', sandbox: false, removedJobAction: 'DELETE')
    }
}
