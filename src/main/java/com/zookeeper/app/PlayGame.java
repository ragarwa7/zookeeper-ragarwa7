package com.zookeeper.app;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PlayGame {

    private static final String MASTER = "/test3";
    private static ZooKeeper zooKeeper;
    private static ZooKConnector zooKConnector;

    protected static List<String> znodelist = new ArrayList<String>();

    public static void main(String[] arg) throws IOException, InterruptedException, KeeperException {
        String command = arg[0];
        String ipAddress = arg[1];
        zooKConnector = new ZooKConnector();
        zooKeeper = zooKConnector.connect(ipAddress);
        initializeMasterNode();
        if(arg.length > 4){
            automatedPlayer(arg);
        }else {
            player(arg);
        }
    }

    private static void player(String[] arg) throws InterruptedException, UnsupportedEncodingException, KeeperException {
        String name = arg[2];
        Player player = new Player();
        Player.zooKConnector = zooKConnector;
        Player.zooKeeper = zooKeeper;
        player.create(name);
    }

    private static void automatedPlayer(String[] arg) throws InterruptedException, UnsupportedEncodingException, KeeperException {
        int n = 0;
        String name = arg[2];
        int count = Integer.parseInt(arg[3]);
        int delay = Integer.parseInt(arg[4]);
        int score = Integer.parseInt(arg[5]);

        Player player = new Player();
        Player.zooKConnector = zooKConnector;
        Player.zooKeeper = zooKeeper;
        player.create(name,count, delay, score);

    }

    private static void initializeMasterNode() throws KeeperException, InterruptedException {
        if(zooKeeper.exists(MASTER,true) == null) {
            zooKeeper.create(MASTER, (MASTER + "/#").getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

}
