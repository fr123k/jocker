node('master') {
    stage('Checkout') {
        cleanWs()
        checkout scm
    }

    stage('Seed') {
        // https://issues.jenkins-ci.org/browse/JENKINS-44142
        // --> Note: when using multiple Job DSL build steps in a single job, set this to "Delete" only for the last Job DSL build step. 
        // Otherwise views may be deleted and re-created. See JENKINS-44142 for details.
        jobDsl(targets: 'shared-library/jobDSL/folders.groovy', sandbox: false, removedJobAction: 'IGNORE')
        jobDsl(targets: 'shared-library/jobDSL/*.groovy', sandbox: false, removedJobAction: 'DELETE')
    }
}
