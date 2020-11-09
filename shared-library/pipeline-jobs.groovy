import groovy.json.JsonSlurperClassic

@NonCPS
def parseJobList(def json) {
    def jobsListB64 = json.replaceAll("\"", "")
    byte[] decoded = jobsListB64.decodeBase64()
    def json_str = new String(decoded)

    def jsonSlurper = new JsonSlurperClassic()
    jobsList = jsonSlurper.parseText(json_str)
    println(jobsList)
    return jobsList
}

node('master') {
    stage('Checkout') {
        cleanWs()
        checkout scm
    }

    stage('Seed') {
        echo "create Jobs based repository: ${repository}, revision: ${jobDSLRevision}, jobDSLCredentialsId: ${jobDSLCredentialsId}, jobDSLPath: ${jobDSLPath}, removedJobAction ${removedJobAction}"
        createJobs("${repository}", "${jobDSLRevision}", "${jobDSLCredentialsId}", "${jobDSLPath}", "${removedJobAction}")

        println env.JOBS_LIST

        if (env.JOBS_LIST) {
            def jobsList = parseJobList(env.JOBS_LIST)

            jobsList.jobs.each{ job ->
                createJobs("${job.repository}", "${job.jobDSLRevision}", "${job.jobDSLCredentialsId}", "${job.jobDSLPath}", "${job.removedJobAction}")
            }
        }
    }
}
