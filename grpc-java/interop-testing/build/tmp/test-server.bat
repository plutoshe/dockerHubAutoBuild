@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  test-server startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

@rem Add default JVM options here. You can also use JAVA_OPTS and TEST_SERVER_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xbootclasspath/p:/home/xwu/.gradle/caches/modules-2/files-2.1/org.mortbay.jetty.alpn/alpn-boot/7.1.2.v20141202/3f2a33d635d8f21fdbbf2d426b12d2fc211785ab/alpn-boot-7.1.2.v20141202.jar"

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windowz variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\grpc-interop-testing-0.8.0-SNAPSHOT.jar;%APP_HOME%\lib\grpc-auth-0.8.0-SNAPSHOT.jar;%APP_HOME%\lib\grpc-core-0.8.0-SNAPSHOT.jar;%APP_HOME%\lib\grpc-netty-0.8.0-SNAPSHOT.jar;%APP_HOME%\lib\grpc-okhttp-0.8.0-SNAPSHOT.jar;%APP_HOME%\lib\grpc-protobuf-0.8.0-SNAPSHOT.jar;%APP_HOME%\lib\grpc-stub-0.8.0-SNAPSHOT.jar;%APP_HOME%\lib\grpc-testing-0.8.0-SNAPSHOT.jar;%APP_HOME%\lib\junit-4.11.jar;%APP_HOME%\lib\mockito-core-1.10.19.jar;%APP_HOME%\lib\google-auth-library-oauth2-http-0.1.0.jar;%APP_HOME%\lib\javaee-api-7.0.jar;%APP_HOME%\lib\guava-18.0.jar;%APP_HOME%\lib\jsr305-3.0.0.jar;%APP_HOME%\lib\netty-codec-http2-4.1.0.Beta5.jar;%APP_HOME%\lib\okhttp-2.4.0.jar;%APP_HOME%\lib\protobuf-java-3.0.0-alpha-3.jar;%APP_HOME%\lib\hamcrest-core-1.3.jar;%APP_HOME%\lib\objenesis-2.1.jar;%APP_HOME%\lib\google-auth-library-credentials-0.1.0.jar;%APP_HOME%\lib\google-http-client-1.19.0.jar;%APP_HOME%\lib\google-http-client-jackson2-1.19.0.jar;%APP_HOME%\lib\javax.mail-1.5.0.jar;%APP_HOME%\lib\netty-codec-http-4.1.0.Beta5.jar;%APP_HOME%\lib\netty-handler-4.1.0.Beta5.jar;%APP_HOME%\lib\okio-1.4.0.jar;%APP_HOME%\lib\httpclient-4.0.1.jar;%APP_HOME%\lib\jackson-core-2.1.3.jar;%APP_HOME%\lib\activation-1.1.jar;%APP_HOME%\lib\netty-codec-4.1.0.Beta5.jar;%APP_HOME%\lib\netty-buffer-4.1.0.Beta5.jar;%APP_HOME%\lib\netty-transport-4.1.0.Beta5.jar;%APP_HOME%\lib\httpcore-4.0.1.jar;%APP_HOME%\lib\commons-logging-1.1.1.jar;%APP_HOME%\lib\commons-codec-1.3.jar;%APP_HOME%\lib\netty-common-4.1.0.Beta5.jar;%APP_HOME%\lib\netty-resolver-4.1.0.Beta5.jar;%APP_HOME%\lib\hpack-0.11.0.jar

@rem Execute test-server
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %TEST_SERVER_OPTS%  -classpath "%CLASSPATH%" io.grpc.testing.integration.TestServiceServer %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable TEST_SERVER_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%TEST_SERVER_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
