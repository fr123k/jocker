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
}
