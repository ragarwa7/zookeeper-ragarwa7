#!/bin/bash

javac -classpath ".:zookeeper-3.4.10.jar" ZooKConnector.java
javac -classpath ".:zookeeper-3.4.10.jar" Play.java
javac -classpath ".:zookeeper-3.4.10.jar" Player.java

if [ $# -eq 5 ]
then
	java -classpath ".:zookeeper-3.4.10.jar:slf4j-log4j12-1.6.1.jar:slf4j-api-1.6.1.jar:log4j-1.2.16.jar" Player $1 $2 $3 $4 $5
elif [ $# -eq 2 ]
then
	java -classpath ".:zookeeper-3.4.10.jar:slf4j-log4j12-1.6.1.jar:slf4j-api-1.6.1.jar:log4j-1.2.16.jar" Player $1 $2 
else
	echo "provide valid arguments"
fi