// Openstack Agent

pipelineJob("jenkins/DockerAgentBootstrap") {

    parameters {
        gitParam('revision') {
            type('BRANCH_TAG')
            sortMode('ASCENDING_SMART')
            defaultValue('origin/master')
        }

        stringParam('node', '', '')
        stringParam('image', '', '')
    }

    logRotator {
        numToKeep(50)
    }

    throttleConcurrentBuilds {
        maxPerNode(6)
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

pipelineJob("jenkins/DockerAgentBuild") {

    parameters {
        gitParam('revision') {
            type('BRANCH_TAG')
            sortMode('ASCENDING_SMART')
            defaultValue('origin/master')
        }

        stringParam('node', '', '')
        stringParam('label', '', '')
        stringParam('image', '', '')
        stringParam('gitUrl', '', '')
        stringParam('gitRevision', '', '')
    }

    logRotator {
        numToKeep(50)
    }

    throttleConcurrentBuilds {
        maxPerNode(6)
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

            scriptPath('shared-library/vars/jenkins/DockerAgentBuild.groovy')
        }
    }
}

pipelineJob("jenkins/DockerAgentDestroy") {

    parameters {
         gitParam('revision') {
            type('BRANCH_TAG')
            sortMode('ASCENDING_SMART')
            defaultValue('origin/master')
        }
        stringParam('node', '', '')
        stringParam('label', '', '')
    }

    logRotator {
        numToKeep(50)
    }

    throttleConcurrentBuilds {
        maxPerNode(6)
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
