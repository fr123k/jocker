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
                        github("/fr123k/jocker", "ssh")
                        credentials("github-ssh-jocker")
                    }

                    branch('$revision')
                }
            }
            
            scriptPath('resources/pipeline.groovy')
        }
    }
}