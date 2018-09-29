@ECHO OFF

SET PROGRAM_BIN_DIR=%~dp0
SET PROGRAM_HOME_DIR=%PROGRAM_BIN_DIR%..
SET MAIN_CLASS=ch.rweiss.jmx.client.cli.JmxClientCli
SET CLASS_PATH=%PROGRAM_HOME_DIR%\lib\*

java.exe -cp %CLASS_PATH% -Djava.library.path=%PROGRAM_BIN_DIR% %MAIN_CLASS% %*