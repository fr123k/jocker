#!groovy

folder('Jenkins') {
    description('Folder containing configuration and seed jobs')
}

pipelineJob("Jenkins/Setup") {
    parameters {
        stringParam('revision_configure', 'origin/master', '')
        stringParam('revision_jobs', 'origin/master', '')
    }

    triggers {
        githubPush()
    }

    logRotator {
        numToKeep(50)
    }

    definition {
        cps {
            script("""
        node ("master") {
            stage("Configure") {
                build(job:'Jenkins/Configure', parameters:[
                    string(name: 'revision', value: params.revision_configure)],
                    propagate:true,
                    wait:true)
            }
            stage("Jobs") {
                build(job:'Jenkins/Jobs', parameters:[
                    string(name: 'revision', value: params.revision_jobs)],
                    propagate:true,
                    wait:true)
            }
        }
            """)
        }
    }
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
                        github("{{ seed-configure-git-repo }}", "ssh")
                        credentials("deploy-key-shared-library")
                    }

                    branch('$revision')
                }
            }
            //shared-library/pipeline.groovy
            scriptPath('{{ seed-configure-groovy-file }}')
        }
    }
}

pipelineJob("Jenkins/Jobs") {
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
                        github("{{ seed-job-git-repo }}", "ssh")
                        credentials("deploy-key-shared-library")
                    }

                    branch('$revision')
                }
            }
            //shared-library/pipeline.groovy
            scriptPath('{{ seed-job-groovy-file }}')
        }
    }
}
