#!groovy

/*
 * This script is designated for the init.groovy.d 
 * directory to be executed at startup time of the 
 * Jenkins instance. This script requires the jobDSL
 * Plugin. Tested with job-dsl:1.70
 */

import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.plugin.JenkinsJobManagement
import jenkins.model.Jenkins
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey
import com.cloudbees.plugins.credentials.CredentialsScope
import hudson.security.FullControlOnceLoggedInAuthorizationStrategy
import hudson.security.HudsonPrivateSecurityRealm
import hudson.model.*
import jenkins.security.*
import jenkins.security.apitoken.*

def domain = Domain.global()
def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()
def keyFileContents = new File("/var/jenkins_home/deployKey/jocker-shared-library-private-key").text
def privateKey = new BasicSSHUserPrivateKey(
  CredentialsScope.GLOBAL,
  "github-ssh-jocker",
  "root",
  new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(keyFileContents),
  "",
  "SSH key for shared-library"
)
store.addCredentials(domain, privateKey)

// Create the configuration pipeline from a jobDSL script
def jobDslScript = new File('/var/jenkins_home/dsl/bootstrap.groovy')
def workspace = new File('.')
def jobManagement = new JenkinsJobManagement(System.out, [:], workspace)
new DslScriptLoader(jobManagement).runScript(jobDslScript.text)
// Schdule the Jenkins/Configure job
Jenkins.instance.getItemByFullName("Jenkins/Configure").scheduleBuild()

println(Jenkins.instance.getSecurityRealm().getClass().getSimpleName())
// Disable Wizards
if(Jenkins.instance.getSecurityRealm().getClass().getSimpleName() == 'None') {
    // def instance = Jenkins.getInstance()
    // def setupUser = "admin"
    // def setupPass = "admin"

    // def hudsonRealm = new HudsonPrivateSecurityRealm(false)
    // instance.setSecurityRealm(hudsonRealm)
    // def user = instance.getSecurityRealm().createAccount(setupUser, setupPass)
    // user.save()

    // def prop = user.getProperty(ApiTokenProperty.class)
    // def result = prop.tokenStore.generateNewToken("token-created-by-init-groovy")
    // user.save()

    // println("###################################################\n## Api-Token: " + result.plainValue + " ##\n###################################################")

    // def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
    // strategy.setAllowAnonymousRead(true)
    // instance.setAuthorizationStrategy(strategy)

    // instance.save()

    println("SetupWizard Disabled")
}
