#! groovy

import jenkins.model.Jenkins

// trigger configuration
System.setProperty('casc.jenkins.config', '/var/jenkins_home/casc-config/')
def jcacPlugin = Jenkins.instance.getExtensionList(io.jenkins.plugins.casc.ConfigurationAsCode.class).first()
jcacPlugin.configure()
