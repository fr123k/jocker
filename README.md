[![Build Status](https://travis-ci.com/fr123k/jocker.svg?branch=master)](https://travis-ci.com/fr123k/jocker)
[![dockeri.co](https://dockeri.co/image/fr123k/jocker)](https://hub.docker.com/r/fr123k/jocker)

# jocker (jenkins in docker)

## Prerequisites

### Makefile

* pwgen
```bash
apt-get install pwgen
```

## Introduction

Mini jenkins docker container that run jenkins as code. The jenkins is configured with
a init.groovy script that does things like.

* disable CSRF protection (remote calls)
* (`Jenkins/Setup`): create and run the seed jobs
    * (`Jenkins/Configure`): run configure as code and custom groovy scripts
    * (`Jenkins/Jobs`): create all the build jobs
* setup the user base (admin/random password user with api token)
    * both is written to the stdout

The seed jobs are defined in 'dsl/bootstrap.groovy' and creates the
following 3 jobs.

* `Jenkins/Setup`

This seed job orchestrate all the other seed jobs see below.

* `Jenkins/Configure`

This seed job that run the configure as code (casc) plugin and custom groovy script that
disable the csrf protection, add script approvals, set timezone, ...

* `Jenkins/Jobs`

This seed job load all the jobDSL files to create the build/pipeline jobs based on the provided jobDSL files.

## Usage

### Build

To build the jenkins in docker image run the listed command below.

```bash
make build
```

### Local Runtime

```bash
make jocker logs
```

[Jenkins](http://localhost:8080/)

### Local Test

```bash
make jocker
sleep 60 #wait until jenkins finish bootsrap and Configure job ran
make test
```

## Jenkins

There are ready to use docker jenkins agents for different programming languages or tools.

*At the moment there are only one docker jenkins agent for golang*

### Jenkins Golang Docker Agents

This docker image provide an ready to use jnlp jenkins agents with golang support.

* installed golang 1.12
* installed pulumi latest version at build time

The jocker-agents-golang docker image is based on this github repo [jocker-agents](https://github.com/fr123k/jocker-agents).

[Jenkins](http://localhost:8080/)

## Structure

```
├── deployKeys
├── dsl
├── init.groovy.d
└── shared-library
```
```
├── Dockerfile
├── Makefile
├── README.md
├── deployKey
│   └── jocker-shared-library-private-key
├── dsl
│   └── bootstrap.groovy
├── init.groovy.d
│   ├── init.groovy
│   └── scriptApproval.xml
├── plugins.txt
└── shared-library
    ├── config
    │   ├── casc-plugin
    │   │   ├── credentials.yaml                # define jenkins credential secrets like deploy key's for example.
    │   │   └── jenkins.yaml
    │   └── groovy
    │       ├── cascPlugin.groovy
    │       ├── csrf.groovy
    │       ├── scriptApproval.groovy
    │       ├── timezone.groovy
    │       └── userPublicKeys.groovy
    ├── jobDSL
    │   └── folders.groovy
    └── pipeline.groovy
```

### Configuration as Code Plugin

The casc (Configuration as Code) plugin overwrites any previous defined configuration. There is no merge strategy.

**Be care full the deploy keys defined in the `init.groovy` file are overwritten by the `credentials.yaml` casc configuration file. That's why the deploy key is defined twice once in the `deploy-key-shared-library` file and in the `credentials.yaml` casc file.**

### Configure Shared Library

The Shared Library contains

### Jenkins CLI

Need open jenkins cli port default is 50000
```bash
curl -sI  http://host.docker.internal:8080
```

## Contribution

Big thank goes to [fishi0x01](https://github.com/fishi0x01).

Who convince and inspired my for the whole jenkins in docker approach and also
builded the first [jenkins-as-code](https://github.com/devtail/jenkins-as-code) poc of it. Most of this repository is build on top of `jenkins-as-code` repository.

## TODO

* add generated admin user api token as credentials to jenkins for further usage in other build jobs
* pass shared library deploy key at runtime

## History

13.02.2020
* configure shared libary git repository as docker environment variable

22.02.2020
* add random password creation for user admin
* add approveScript method signature to the scriptApproval.xml file

28.02.2020
* add ADMIN_PASSWORD environment variable to set the admin user password
01.03.2020
* fix broken admin password generation in init.groovy script.
