SET JmCliBinDir=%~dp0
SET JmCliHome=%JmCliBinDir%..
SET MainClass=ch.rweiss.jmx.client.cli.JmxClientCli
SET ClassPath=%JmCliHome%\lib\*

java.exe -cp %ClassPath% -Djava.library.path=%JmCliBinDir% %MainClass% %*