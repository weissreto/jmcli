SET JAVA_HOME=c:\program files\java\jdk-9.0.1
SET MAVEN=C:\Tools\maven\apache-maven-3.5.2\bin\mvn
SET MAIN_CLASS=ch.weiss.jmx.client.cli.JmxClientCli

%MAVEN% exec:java -Dexec.mainClass=%MAIN_CLASS% -Dexec.args="%*" -Dexec.classpathScope=test
