node('master') {
    stage('Checkout') {
        cleanWs()
        checkout scm
    }

    stage('Seed') {
        echo "create Jobs based repository:${repository}, revision: ${jobDSLRevision}, jobDSLPath: ${jobDSLPath}"
        createJobs("${repository}", "${jobDSLRevision}", "${jobDSLPath}")
    }
}
