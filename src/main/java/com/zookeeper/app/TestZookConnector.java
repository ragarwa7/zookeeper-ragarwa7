package com.zookeeper.app;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestZookConnector {

    private static ZooKeeper zooKeeper;
    private static ZooKConnector zooKConnector;

    protected static List<String> znodelist = new ArrayList<String>();

    public static void main(String[] arg) throws IOException, InterruptedException, KeeperException {
        zooKConnector = new ZooKConnector();
        zooKeeper = zooKConnector.connect("localhost");

        znodelist = zooKeeper.getChildren("/", true);
        for(String znode: znodelist){
            System.out.println(znode);
        }
    }

}
