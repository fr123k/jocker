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

# Disable the setup wizard 
ENV JAVA_OPTS "-Djenkins.install.runSetupWizard=false ${JAVA_OPTS:-}"

ENV SEED_CONFIGURE_GIT_REPO fr123k/jocker
ENV SEED_CONFIGURE_GROOVY_FILE shared-library/pipeline.groovy

ENV SEED_JOB_GIT_REPO fr123k/jocker
ENV SEED_JOB_GROOVY_FILE shared-library/pipeline-jobs.groovy

ENV JENKINS_CLI /var/jenkins_home/war/WEB-INF/jenkins-cli.jar
