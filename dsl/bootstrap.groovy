#!groovy

folder('Jenkins') {
    description('Folder containing configuration and seed jobs')
}

pipelineJob("Jenkins/Configure") {
    parameters {
        gitParam('revision') {
            type('BRANCH_TAG')
            sortMode('ASCENDING_SMART')
            defaultValue('origin/master')
        }
    }

    triggers {
        githubPush()
    }

    logRotator {
        numToKeep(50)
    }

    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        github("{{ shared-library-git-repo }}", "ssh")
                        credentials("deploy-key-shared-library")
                    }

                    branch('$revision')
                }
            }
            //shared-library/pipeline.groovy
            scriptPath('{{ shared-library-groovy-file }}')
        }
    }
}
