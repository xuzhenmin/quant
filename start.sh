#!/usr/bin/env bash
export MAVEN_HOME=/opt/apache-maven-3.9.9
export PATH=$MAVEN_HOME/bin:$PATH

nohup mvn jetty:run > stdout.log 2> stderr.log &
