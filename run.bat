SET JAVA_HOME=c:\program files\java\jdk-11
SET MAVEN=C:\Tools\maven\apache-maven-3.5.2\bin\mvn
SET MAIN_CLASS=ch.rweiss.jmcli.JmCli

%MAVEN% exec:java -Dexec.mainClass=%MAIN_CLASS% -Dexec.args="%*" -Dexec.classpathScope=test
