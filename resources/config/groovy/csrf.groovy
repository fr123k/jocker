#! groovy
import jenkins.model.Jenkins

//TODO configure the disabling of csrf in the casc configuration
Jenkins.getInstance().setCrumbIssuer(null)
println("Disable CSRF protection")
