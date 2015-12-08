@ECHO OFF
cd C:\Program Files\yunPrint
SET PATH=./jre/bin;%1;%PATH%;
start ./jre/bin/java -jar  printer-1.0.jar
