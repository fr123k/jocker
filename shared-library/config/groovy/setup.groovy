@Library('shared-lib')

import org.jocker.setup.SetupWizard

def call() {
    echo "${workspace}/shared-library/config/casc-config/"
    def setupWizard = new SetupWizard("${workspace}/shared-library/config/casc-config/")
                        .setup()
    return setupWizard
}

return this
