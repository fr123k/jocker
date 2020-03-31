views = [
    [
        name: "jenkins",
        description: "The jenkins configuration jobs to bootstrap a ready to use jenkins.",
        projects: [
            "jenkins",
        ],
    ],
]

for(view in views) {
    listView("${view.name}") {
        description("${view.description}")
        filterBuildQueue()
        filterExecutors()
        jobs {
            for(project in view.projects) {
                name("${project}")
            }
        }
        columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
        }
    }
}
