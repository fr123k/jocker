#! groovy

def user = hudson.model.User.get('admin')

def jenkinsCliPubKeyContent = new File('/var/jenkins_home/.ssh/jenkins-cli.pub').text


def pubKey = new org.jenkinsci.main.modules.cli.auth.ssh.UserPropertyImpl(jenkinsCliPubKeyContent)
user.addProperty(pubKey)

user.save()
