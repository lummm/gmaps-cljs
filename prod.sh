#!/bin/sh

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_152.jdk/Contents/Home
rm -r target/*
boot prod
sass resources/styles/app.scss target/styles/app.css
