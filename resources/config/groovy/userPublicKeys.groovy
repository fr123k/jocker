#! groovy

def user = hudson.model.User.get('admin')

def pubKey = new org.jenkinsci.main.modules.cli.auth.ssh.UserPropertyImpl(System.getenv()['JENKINS_CLI_PUBLIC_KEY'])
user.addProperty(pubKey)

user.save()
