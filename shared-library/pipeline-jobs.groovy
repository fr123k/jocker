node('master') {
    stage('Checkout') {
        cleanWs()
        checkout scm
    }

    stage('Seed') {
        echo "create Jobs based repository: ${repository}, revision: ${jobDSLRevision}, jobDSLPath: ${jobDSLPath}, removedJobAction ${removedJobAction}"
        createJobs("${repository}", "${jobDSLRevision}", "${jobDSLPath}", "${removedJobAction}")
    }
}
