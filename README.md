# Java Management Command Line Interface (jmcli) [![Build Status](https://travis-ci.org/weissreto/jmx-cli.svg?branch=master)](https://travis-ci.org/weissreto/jmx-cli)

The jmcli is a command line tool that displays information about Java virtual maschines in your console.

## Download / Installation

1. Download the zip file for your platform
  * [Windows](../../releases/download/v0.1.0/jmx-cli-0.1.0-windows.zip) 
  * [Linux](../../releases/download/v0.1.0/jmx-cli-0.1.0-linux.zip)  
2. Unzip the downloaded zip file to a new installation directory 
3. Add the `bin` directory inside the installation directory to the system path
 
## Help

Use `jmcli -h` to print the help.

## Available Commands

This are the commands you can use:

| Command | Description | Example |
| ------- | ----------- | ------- |
| list vm | Lists all available java virtual maschines | ![listvm](doc/listvm.png) |
| list beans | Lists all available management beans | ![listvm](doc/listbeans.png) |
| list attributes | List attributes | ![listvm](doc/listattributes.png) |
| list threads | List all threads | ![listvm](doc/listthreads.png) |
| list threads-states | List all threads and their states | ![listvm](doc/listthreadsstates.png) |
| list classes | List all loaded classes and the number of instances | ![listvm](doc/listclasses.png) |
| info vm | Prints information about a virtual machine | |
| info bean | Prints information about managment beans | |
| info attribute | Prints information about attributes | |
| info operation | Prints information about operations | |
| set attribute | Sets the value of an attribute | |
| invoke operation | Invokes an operation | |
| chart | Draws a chart | ![listvm](doc/chart.png) |
| dashboard | Draws a dashboard | ![listvm](doc/dashboard.png) |

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

Currently only Java 8 is support to execute jmcli