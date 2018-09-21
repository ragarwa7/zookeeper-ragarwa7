# zookeeper-ragarwa7
Zookeeper HW1 - Data Intensive Computing

## Prerequisites
    a. Java version 1.8 & Zookeeper updated version should be installed in the machine.
    b. Start Zookeeper Server using command "zkServer.cmd" and client using command "zkCli.cmd"
    c. Follow below instruction to execute the code.
    
## Execution steps using pre-compiled jar files
    a. Download player.jar & watcher.jar  
    b. In command window, navigate to the directory where the jars are downloaded.
    c. Start Zookeeper Server using command "zkServer.cmd" and client using command "zkCli.cmd"
    d. Start Watcher following below syntax:
       java -cp watcher.jar Watcher 12.34.45.87:6666 N
    e. Start Player following below syntax:
             -> java -cp player.jar Player 12.34.45.87:6666 name
             -> java -cp player.jar Player 12.34.45.87:6666 name count delay score
          
         
