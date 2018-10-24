# Java Management Command Line Interface (jmcli) [![Build Status](https://travis-ci.org/weissreto/jmx-cli.svg?branch=master)](https://travis-ci.org/weissreto/jmx-cli)

The jmcli is a command line tool that displays information about Java virtual maschines in your console.

![dashboard](doc/dashboard.png)

## Download / Installation

1. Download the zip file for your platform
  * [Java 11 - Windows](../../releases/download/v0.2.0/jmx-cli-0.2.0-windows.zip) 
  * [Java 11 - Linux](../../releases/download/v0.2.0/jmx-cli-0.2.0-linux.zip)
  * [Java 8 - Windows](../../releases/download/v0.2.0/jmx-cli-0.2.0-java8-windows.zip) 
  * [Java 8 - Linux](../../releases/download/v0.2.0/jmx-cli-0.2.0-java8-linux.zip)    
2. Unzip the downloaded zip file to a new installation directory 
3. Add the `bin` directory inside the installation directory to the system path

## Docker

You can use Docker to play with and learn about jmcli. 
Of course, you can use jmcli inside your Docker container to monitor your Java service 

```bash
 docker run -it rweiw/jmcli:latest
 jmcli -V
```
 
## Help

Use `jmcli -h` to print the help.

## Available Commands

This are the commands you can use:

| Command | Description | Example |
| ------- | ----------- | ------- |
| list vm | Lists all available java virtual maschines | ![listvm](doc/listvm.png) |
| list beans | Lists all available management beans | ![listbeans](doc/listbeans.png) |
| list attributes | List attributes | ![listattributes](doc/listattributes.png) |
| list threads | List all threads | ![listthreads](doc/listthreads.png) |
| list threads-states | List all threads and their states | ![listthreadsstates](doc/listthreadsstates.png) |
| list classes | List all loaded classes and the number of instances | ![listclasses](doc/listclasses.png) |
| info vm | Prints information about a virtual machine | |
| info bean | Prints information about managment beans | |
| info attribute | Prints information about attributes | |
| info operation | Prints information about operations | |
| set attribute | Sets the value of an attribute | |
| invoke operation | Invokes an operation | |
| chart | Draws a chart | ![chart](doc/chart.png) |
| dashboard | Draws a dashboard | ![dashboard](doc/dashboard.png) |

## Important Options

| Short Option | Long Option | Description |
| ------------ | ----------- | ----------- | 
| -h | --help | Displays the help |
| -j={jvm} | --jvm={jvm} | Process identifier or part of the main class of the virtual maschine to connect to |
| -i={interval} | --interval={interval} | Refresh interval in seconds |
| -v | --verbose | Displays detail messages |
| -V | --version | Displays version information |

## Requirements

### Operating System

The following Operating Systems are supported:
* Windows 10
* Linux

### Java

The following Java versions are supported:
* Java 8  (needs an installed JRE) 
* Java 11 (needs a full JDK or at least a JRE and additionally the jdk.attach module)