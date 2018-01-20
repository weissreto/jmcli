#!/bin/bash

MAIN_CLASS=ch.rweiss.jmx.client.cli.JmxClientCli

mvn exec:java -Dexec.mainClass=$MAIN_CLASS -Dexec.args="$*" -Dexec.classpathScope=test
