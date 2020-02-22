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

// for generate randome alphanumeric strings
def generator = { String alphabet, int n ->
  new Random().with {
    (1..n).collect { alphabet[ nextInt( alphabet.length() ) ] }.join()
  }
}

def domain = Domain.global()
def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

//Generate a private/public key pair for jenkins-cli authention used by the agent bootstrap
def proc = ['ssh-keygen','-t', 'rsa','-b','4096','-q','-N','', '-f', '/var/jenkins_home/.ssh/jenkins-cli'].execute();
proc.waitForProcessOutput(System.out, System.err);

def keyFiles = new File("/var/jenkins_home/deployKeys/").listFiles();
for (File privateKeyFile : keyFiles) {
    def keyFileContents = privateKeyFile.text
    System.out.println(privateKeyFile.getName())
    def privateKey = new BasicSSHUserPrivateKey(
      CredentialsScope.GLOBAL,
      privateKeyFile.getName(),
      "root",
      new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(keyFileContents),
      "",
      "SSH key for " + privateKeyFile.getName()
    )
    store.addCredentials(domain, privateKey)
}

def env = System.getenv()

// Create the configuration pipeline from a jobDSL script
def jobDslScriptContent = new File('/var/jenkins_home/dsl/bootstrap.groovy').text
jobDslScriptContent = jobDslScriptContent.replace('{{ shared-library-git-repo }}', env['BOOTSTRAP_SHARED_LIBRARY_GIT_REPO'])
jobDslScriptContent = jobDslScriptContent.replace('{{ shared-library-groovy-file }}', env['BOOTSTRAP_SHARED_LIBRARY_GROOVY_FILE'])

def workspace = new File('.')
def jobManagement = new JenkinsJobManagement(System.out, [:], workspace)
new DslScriptLoader(jobManagement).runScript(jobDslScriptContent)

// Schdule the Jenkins/Configure job
// Use the provided SEED_BRANCH environment vairable if specified
def ENV_SEED_BRANCH = env['SEED_BRANCH']
def seedRevision = ENV_SEED_BRANCH ?: "origin/master"
Jenkins.instance.getItemByFullName("Jenkins/Configure").scheduleBuild2(1, new ParametersAction([ new StringParameterValue("revision", seedRevision)]))

println(Jenkins.instance.getSecurityRealm().getClass().getSimpleName())
// Disable Wizards
if(Jenkins.instance.getSecurityRealm().getClass().getSimpleName() == 'None') {
    def instance = Jenkins.getInstance()
    def setupUser = "admin"
    def setupPass = generator( (('A'..'Z')+('0'..'9')+('a'..'z')).join(), 15 )

    println("###################################################\nPassword:" + setupPass + "\n###################################################")

    def hudsonRealm = new HudsonPrivateSecurityRealm(false)
    instance.setSecurityRealm(hudsonRealm)
    def user = instance.getSecurityRealm().createAccount(setupUser, setupPass)
    user.save()

    //Create api token for the user for jenkins remote calls
    def prop = user.getProperty(ApiTokenProperty.class)
    def result = prop.tokenStore.generateNewToken("token-created-by-init-groovy")
    user.save()

    println("###################################################\nApi-Token:" + result.plainValue + "\n###################################################")

    def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
    strategy.setAllowAnonymousRead(false)
    instance.setAuthorizationStrategy(strategy)

    instance.save()

    println("SetupWizard Disabled")
}
