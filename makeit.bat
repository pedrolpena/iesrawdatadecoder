echo off

javac -source 1.6 -target 1.6 -d .\ src\rawdatadecoder\*.java
jar cfm RawDataDecoder.jar manifest.txt rawdatadecoder\*.class

IF EXIST .\dist goto deletedist

:deletedist
del /q /s .\dist  > nul
rmdir /q /s .\dist  > nul
:exit

mkdir .\dist
mkdir .\dist\lib
move /y RawDataDecoder.jar .\dist > nul
copy /y .\lib .\dist\lib > nul
del /s /q .\rawdatadecoder  > nul
rmdir /s /q .\rawdatadecoder  > nul


