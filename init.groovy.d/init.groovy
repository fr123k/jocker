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

def adminPassword = { ->
  return System.getenv()['ADMIN_PASSWORD'] ?: generator( (('A'..'Z')+('0'..'9')+('a'..'z')).join(), 15 )
}

def parseJobDslScript = { List<String> variableNames ->
  def env = System.getenv()
  // Create the configuration pipeline from a jobDSL script
  def jobDslScript = new File('/var/jenkins_home/dsl/bootstrap.groovy').text
  variableNames.each { variableName ->
    def environmentName = variableName.toUpperCase().replaceAll("-", "_")
    println("JobDSl Parser : replace " + variableName + " " + environmentName)
    println("JobDSl Parser : env " + env[environmentName])
    jobDslScript = jobDslScript.replace("{{ ${variableName} }}", env[environmentName])
  }
  return jobDslScript
}

def domain = Domain.global()
def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

//Generate a private/public key pair for jenkins-cli authention used by the agent bootstrap
def proc = ['ssh-keygen','-t', 'rsa','-b','4096','-q','-N','', '-f', '/var/jenkins_home/.ssh/jenkins-cli'].execute();
proc.waitForProcessOutput(System.out, System.err);

def keyFiles = new File("/var/jenkins_home/deployKeys/").listFiles();
for (File privateKeyFile : keyFiles) {
    def keyFileContents = privateKeyFile.text
    def (username, keyName)  = privateKeyFile.getName().split("\\.")
    System.out.println(privateKeyFile.getName())
    def privateKey = new BasicSSHUserPrivateKey(
      CredentialsScope.GLOBAL,
      keyName,
      username,
      new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(keyFileContents),
      "",
      "SSH key for " + keyName
    )
    store.addCredentials(domain, privateKey)
}

def jobDslVariables = ['seed-shared-lib-git-repo', 'seed-shared-lib-groovy-file', 'seed-configure-git-repo', 'seed-configure-groovy-file', 'seed-job-git-repo', 'seed-job-groovy-file', 'job-dsl-git-repo', 'job-dsl-path']

jobDslScript = parseJobDslScript(jobDslVariables)

def workspace = new File('.')
def jobManagement = new JenkinsJobManagement(System.out, [:], workspace)
new DslScriptLoader(jobManagement).runScript(jobDslScript)

println(Jenkins.instance.getSecurityRealm().getClass().getSimpleName())
// Disable Wizards
if(Jenkins.instance.getSecurityRealm().getClass().getSimpleName() == 'None') {
    def instance = Jenkins.getInstance()
    def setupUser = "admin"
    def setupPass = adminPassword()

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

def env = System.getenv()
// Schdule the Jenkins/Configure job
// Use the provided SEED_BRANCH environment vairables if specified
def seedRevisionConfigure = env['SEED_BRANCH_CONFIGURE'] ?: "origin/master"
def seedRevisionJobs = env['SEED_BRANCH_JOBS'] ?: "origin/master"

Jenkins.instance.getItemByFullName("Jenkins/Setup")
  .scheduleBuild2(1,
    new ParametersAction([
      new StringParameterValue("revision_configure", seedRevisionConfigure),
      new StringParameterValue("revision_jobs", seedRevisionJobs)
    ]
  )
)

def useLocalGit = env['LOCAL_GIT'] ?: false
if (useLocalGit) {
  def file = new File('/var/jenkins_home/.gitconfig')
  file.text = '''
[url "ssh://git@local.github.com/"]
	insteadOf = https://github.com/

[url "ssh://git@local.github.com/"]
	insteadOf = git@github.com:
'''
}
