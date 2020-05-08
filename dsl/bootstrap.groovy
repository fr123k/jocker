#!groovy

folder('jenkins') {
    description('Folder containing configuration and seed jobs')
}

pipelineJob("jenkins/Setup") {
    parameters {
        stringParam('revision_configure', 'origin/master', '')
        stringParam('revision_jobs', 'origin/master', '')
    }

    properties {
        pipelineTriggers {
            triggers {
                githubPush()
            }
        }
    }


    logRotator {
        numToKeep(50)
    }

    definition {
        cps {
            script("""
        node ("master") {
            stage("SharedLib") {
                build(job:'jenkins/SharedLib', parameters:[
                    string(name: 'revision', value: params.revision_configure)],
                    propagate:true,
                    wait:true)
            }
            stage("Configure") {
                build(job:'jenkins/Configure', parameters:[
                    string(name: 'revision', value: params.revision_configure)],
                    propagate:true,
                    wait:true)
            }
            stage("Jobs") {
                build(job:'jenkins/Jobs', parameters:[
                    string(name: 'revision', value: params.revision_jobs)],
                    propagate:true,
                    wait:true)
            }
        }
            """)
        }
    }
}

pipelineJob("jenkins/SharedLib") {
    parameters {
        gitParam('revision') {
            type('BRANCH_TAG')
            sortMode('ASCENDING_SMART')
            defaultValue('origin/master')
        }
    }

    properties {
        pipelineTriggers {
            triggers {
                githubPush()
            }
        }
    }

    logRotator {
        numToKeep(50)
    }

    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        github("{{ seed-shared-lib-git-repo }}", "ssh")
                        credentials("deploy-key-shared-library")
                    }

                    branch('$revision')
                }
            }
            //shared-library/pipeline-shared-lib.groovy
            scriptPath('{{ seed-shared-lib-groovy-file }}')
        }
    }
}

pipelineJob("jenkins/Configure") {
    parameters {
        gitParam('revision') {
            type('BRANCH_TAG')
            sortMode('ASCENDING_SMART')
            defaultValue('origin/master')
        }
    }

    properties {
        pipelineTriggers {
            triggers {
                githubPush()
            }
        }
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

pipelineJob("jenkins/Jobs") {
    parameters {
        gitParam('revision') {
            type('BRANCH_TAG')
            sortMode('ASCENDING_SMART')
            defaultValue('origin/master')
        }
        stringParam('repository', 'https://github.com/{{ job-dsl-git-repo }}', 'The full git url where the jobdsl files are located.')
        stringParam('jobDSLRevision', 'master', 'The revision of the jobDSL repository.')
        stringParam('jobDSLPath', '{{ job-dsl-path }}', 'The path of the jobDSL files.')
        stringParam('removedJobAction', 'DELETE', 'Defines how to the jobDSL defined jobs with the existing once the=y are not part og the defined jobDSL. (IGNORE, DISABLE, DELETE)')
    }

    properties {
        pipelineTriggers {
            triggers {
                githubPush()
            }
        }
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
