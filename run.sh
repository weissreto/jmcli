#!/bin/bash

MAIN_CLASS=ch.rweiss.jmcli.JmCli

mvn exec:java -Dexec.mainClass=$MAIN_CLASS -Dexec.args="$*" -Dexec.classpathScope=test
