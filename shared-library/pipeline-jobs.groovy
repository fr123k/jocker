import groovy.json.JsonSlurper

node('master') {
    stage('Checkout') {
        cleanWs()
        checkout scm
    }

    stage('Seed') {
        echo "create Jobs based repository: ${repository}, revision: ${jobDSLRevision}, jobDSLPath: ${jobDSLPath}, removedJobAction ${removedJobAction}"
        createJobs("${repository}", "${jobDSLRevision}", "${jobDSLPath}", "${removedJobAction}")

        println env.JOBS_LIST

        if (env.JOBS_LIST) {
            def jobsListB64 = env.JOBS_LIST.replaceAll("\"", "")
            byte[] decoded = jobsListB64.decodeBase64()
            def json_str = new String(decoded)

            def jsonSlurper = new JsonSlurper()
            jobsList = jsonSlurper.parseText(json_str)
            println(jobsList)

            jobsList.jobs.each{ job ->
                createJobs("${job.repository}", "${job.jobDSLRevision}", "${job.jobDSLPath}", "${job.removedJobAction}")
            }
        }
    }
}
