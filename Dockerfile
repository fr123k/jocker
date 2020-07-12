FROM jenkins/jenkins:lts

USER root
RUN apt-get update && \
    apt-get install -y rsync docker

USER jenkins

# Install plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

# Add minimum jenkins setup
COPY --chown=jenkins init.groovy.d /usr/share/jenkins/ref/init.groovy.d
COPY --chown=jenkins dsl /usr/share/jenkins/ref/dsl
COPY --chown=jenkins deployKeys /usr/share/jenkins/ref/deployKeys
COPY --chown=jenkins init.groovy.d/scriptApproval.xml /usr/share/jenkins/ref/
# COPY --chown=jenkins config /usr/share/jenkins/ref/

RUN ls -lha /usr/share/jenkins/ref/

# Add custom plugin agents-ondemand
COPY --chown=jenkins plugins/job-agents-on-demand.jpi /usr/share/jenkins/ref/plugins/
RUN touch /usr/share/jenkins/ref/plugins/job-agents-on-demand.jpi.pinned
COPY --chown=jenkins plugins/PrioritySorter.jpi /usr/share/jenkins/ref/plugins/
RUN touch /usr/share/jenkins/ref/plugins/PrioritySorter.pinned

# Disable the setup wizard 
ENV JAVA_OPTS "-Xmx2g -XX:MaxPermSize=512m -Xmx256m -Xms256m -XX:+UseG1GC -Djenkins.install.runSetupWizard=false -Dhudson.model.ParametersAction.keepUndefinedParameters=false ${JAVA_OPTS:-}"

ENV SEED_SHARED_LIB_GIT_REPO fr123k/jocker
ENV SEED_SHARED_LIB_GROOVY_FILE shared-library/pipeline-shared-lib.groovy

ENV SEED_CONFIGURE_GIT_REPO fr123k/jocker
ENV SEED_CONFIGURE_GROOVY_FILE shared-library/pipeline-configure.groovy

ENV SEED_JOB_GIT_REPO fr123k/jocker
ENV SEED_JOB_GROOVY_FILE shared-library/pipeline-jobs.groovy

ENV JOB_DSL_GIT_REPO fr123k/jocker
ENV JOB_DSL_PATH shared-library/jobDSL

ENV SHARED_LIBRARY git@github.com:fr123k/jenkins-shared-library

ENV JENKINS_CLI /var/jenkins_home/war/WEB-INF/jenkins-cli.jar

ENV JENKINS_MASTER http://host.docker.internal:8080
