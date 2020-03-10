pipelineJob("Pulumi") {
    logRotator {
        numToKeep(50)
    }

    definition {
        cps {
            script("""
node ("docker-1") {
    sh("go version")
    sh("pulumi version")
}
            """)
        }
    }
}
