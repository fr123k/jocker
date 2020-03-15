import jenkins.model.Jenkins
import jenkins.plugins.git.GitSCMSource
import jenkins.plugins.git.traits.BranchDiscoveryTrait
import org.jenkinsci.plugins.workflow.libs.GlobalLibraries
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever

import static org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval.get

//used in sharedLibrary.groovy
get().approveSignature('staticMethod java.lang.System getenv')
get().approveSignature('staticMethod jenkins.model.Jenkins getInstance')
get().approveSignature('method jenkins.model.Jenkins getExtensionList java.lang.Class')
get().approveSignature('method hudson.model.Saveable save')
get().approveSignature('new jenkins.plugins.git.GitSCMSource java.lang.String')



get().approveSignature('new jenkins.plugins.git.GitSCMSource java.lang.String')
get().approveSignature('method jenkins.plugins.git.GitSCMSource setCredentialsId java.lang.String')
get().approveSignature('new jenkins.plugins.git.traits.BranchDiscoveryTrait')
get().approveSignature('method jenkins.scm.api.SCMSource setTraits java.util.List')
get().approveSignature('new org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever jenkins.scm.api.SCMSource')
get().approveSignature('new org.jenkinsci.plugins.workflow.libs.LibraryConfiguration java.lang.String org.jenkinsci.plugins.workflow.libs.LibraryRetriever')
get().approveSignature('method org.jenkinsci.plugins.workflow.libs.LibraryConfiguration setDefaultVersion java.lang.String')
get().approveSignature('method org.jenkinsci.plugins.workflow.libs.LibraryConfiguration setImplicit boolean')
get().approveSignature('method org.jenkinsci.plugins.workflow.libs.LibraryConfiguration setAllowVersionOverride boolean')
get().approveSignature('method org.jenkinsci.plugins.workflow.libs.LibraryConfiguration setIncludeInChangesets boolean')
get().approveSignature('method org.jenkinsci.plugins.workflow.libs.GlobalLibraries setLibraries java.util.List')
get().approveSignature('method org.jenkinsci.plugins.workflow.libs.GlobalLibraries getLibraries')
get().approveSignature('method org.jenkinsci.plugins.workflow.libs.LibraryConfiguration getName')
get().approveSignature('method org.jenkinsci.plugins.workflow.libs.LibraryConfiguration getName')
get().approveSignature('method org.jenkinsci.plugins.workflow.libs.LibraryConfiguration getName')
get().approveSignature('method org.jenkinsci.plugins.workflow.libs.LibraryConfiguration getName')
get().approveSignature('method org.jenkinsci.plugins.workflow.libs.LibraryConfiguration getName')
get().approveSignature('method org.jenkinsci.plugins.workflow.libs.LibraryConfiguration getName')
get().approveSignature('method org.jenkinsci.plugins.workflow.libs.LibraryConfiguration getName')


List libraries = [] as ArrayList

def remote = System.getenv()['SHARED_LIBRARY']
def credentialsId = "deploy-key-shared-library"

if (remote != null) {
    name = 'shared-lib'
    defaultVersion = 'master'

    def scm = new GitSCMSource(remote)
    if (credentialsId != null) {
        scm.credentialsId = credentialsId
    }

    scm.traits = [new BranchDiscoveryTrait()]
    def retriever = new SCMSourceRetriever(scm)

    def library = new LibraryConfiguration(name, retriever)
    library.defaultVersion = defaultVersion
    library.implicit = true
    library.allowVersionOverride = true
    library.includeInChangesets = false

    libraries << library

    def global_settings = Jenkins.instance.getExtensionList(GlobalLibraries.class)[0]
    global_settings.libraries = libraries
    global_settings.save()
    println 'Configured Pipeline Global Shared Libraries:\n    ' + global_settings.libraries.collect { it.name }.join('\n    ')
}
