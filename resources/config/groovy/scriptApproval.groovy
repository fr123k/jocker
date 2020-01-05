#! groovy
import static org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval.get

//used in csrf.groovy
get().approveSignature('staticMethod jenkins.model.Jenkins getInstance')
get().approveSignature('method jenkins.model.Jenkins setCrumbIssuer hudson.security.csrf.CrumbIssuer')

//used in cascPlugin.groovy
get().approveSignature('method jenkins.model.Jenkins getExtensionList java.lang.Class')
get().approveSignature('method io.jenkins.plugins.casc.ConfigurationAsCode configure')

//used in userPublicKeys.groovy
get().approveSignature('staticMethod hudson.model.User get java.lang.String')
get().approveSignature('staticMethod java.lang.System getenv')
get().approveSignature('new org.jenkinsci.main.modules.cli.auth.ssh.UserPropertyImpl java.lang.String')
get().approveSignature('method hudson.model.User addProperty hudson.model.UserProperty')
get().approveSignature('method hudson.model.Saveable save')

//used in timezone.groovy
get().approveSignature('staticMethod org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval get')
get().approveSignature('staticMethod java.lang.System setProperty java.lang.String java.lang.String')
