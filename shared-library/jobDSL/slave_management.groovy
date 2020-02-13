// Openstack Agent

pipelineJob("Jenkins/DockerAgentBootstrap") {

    parameters {
        gitParam('revision') {
            type('BRANCH_TAG')
            sortMode('ASCENDING_SMART')
            defaultValue('origin/master')
        }

        stringParam('agentID', '', '')
        stringParam('image', '', '')
    }

    logRotator {
        numToKeep(50)
    }

    throttleConcurrentBuilds {
        maxTotal(1)
    }

    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        github("fr123k/jocker", "ssh")
                        credentials("deploy-key-shared-library")
                    }

                    branch('$revision')
                }
            }

            scriptPath('shared-library/vars/jenkins/DockerAgentBootstrap.groovy')
        }
    }
}

pipelineJob("Jenkins/DockerAgentDestroy") {

    parameters {
         gitParam('revision') {
            type('BRANCH_TAG')
            sortMode('ASCENDING_SMART')
            defaultValue('origin/master')
        }

        stringParam('agentID', '', '')
        stringParam('image', '', '')
    }

    logRotator {
        numToKeep(50)
    }

    throttleConcurrentBuilds {
        maxTotal(1)
    }

    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        github("fr123k/jocker", "ssh")
                        credentials("deploy-key-shared-library")
                    }

                    branch('$revision')
                }
            }

            scriptPath('shared-library/vars/jenkins/DockerAgentDestroy.groovy')
        }
    }
}
