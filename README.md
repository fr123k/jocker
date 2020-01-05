[![Build Status](https://travis-ci.com/fr123k/jocker.svg?branch=master)](https://travis-ci.com/fr123k/jocker)

# jocker (jenkins in docker)

## Introduction

Mini jenkins docker container that run jenkins as code. The jenkins is configured with
a init.groovy script that does things like.

* disable CSRF protection (remote calls)
* create and run the seed job (`Jenkins/Configure`)
* setup the user base (admin/admin user with api token)

The seed job `Jenkins/Configure` is defined in 'dsl/bootstrap.groovy' and creates the
following 3 jobs.

* `Jenkins/Configure`

The seed job that can create all the other jobs. For a simple setup the seed job does nothing. The test job are defined inside the seed job definition.

## Build

To build the jenkins in docker image run the listed command below.

```bash
make build
```

## Usage

```bash
make jocker logs
```
[Jenkins](http://localhost:8080/)


## Contribution

Big thank goes to [fishi0x01-gc](https://github.com/fishi0x01-gc).
Who convince and inspired my for the whole jenkins in docker approach and also
builded the first poc of it. Most of this repository is build on top of `fishi0x01-gc`
poc.
