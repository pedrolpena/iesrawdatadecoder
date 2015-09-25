#!/bin/bash
javac -source 1.6 -target 1.6 -d ./ src/rawdatadecoder/*.java
jar cfm RawDataDecoder.jar manifest.txt rawdatadecoder/*.class 
if [ -d "dist" ]; then
    rm -r dist
fi
mkdir ./dist
rm -r ./rawdatadecoder
mv ./RawDataDecoder.jar ./dist



