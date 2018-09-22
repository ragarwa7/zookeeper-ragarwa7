#!/bin/bash

javac -classpath ".:zookeeper-3.4.10.jar" ZooKConnector.java
javac -classpath ".:zookeeper-3.4.10.jar" Watcher.java

if [ $# -eq 2 ]
then
	java -classpath ".:zookeeper-3.4.10.jar:slf4j-log4j12-1.6.1.jar:slf4j-api-1.6.1.jar:log4j-1.2.16.jar" Watcher $1 $2
else
	echo "provide valid arguments"
fi