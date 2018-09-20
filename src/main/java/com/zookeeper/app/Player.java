package com.zookeeper.app;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import static com.zookeeper.app.Player.MASTER;

public class Player {

    public static ZooKeeper zooKeeper;
    public static ZooKConnector zooKConnector;
    public static final String MASTER = "/test3";
    int exitCount = 0;

    public static byte[] read(String path1) throws KeeperException, InterruptedException {
        return zooKeeper.getData(path1, true, zooKeeper.exists(path1, true));
    }

    public void create(String name) throws KeeperException, InterruptedException, UnsupportedEncodingException {

        String path = MASTER + "/" + name;
        if(zooKeeper.exists(path, true) != null){
            System.out.println("Player already exist");
            System.exit(0);
        }else {
                Scanner reader = new Scanner(System.in);  // Reading from System.in
                System.out.println("Enter a score: ");
                String score = reader.next();
                zooKeeper.create(path, score.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                updateMaster(name, score);
                ExitPlayer.zooKeeper = zooKeeper;
                ExitPlayer exitPlayer = new ExitPlayer(name);
                Runtime.getRuntime().addShutdownHook(exitPlayer);
                while (true) {
                        System.out.println("Add more scores: ");
                        score = reader.next();
                        update(path, score);
                        updateMaster(name, score);
                    }
        }
    }

    public static void update(String path, String data) throws KeeperException, InterruptedException, UnsupportedEncodingException {
        if(!data.matches("-?(0|[1-9]\\d*)")){
            System.out.println("Provide a valid score");
        }else {
            byte[] readScore = read(path);
            String scores = new String(readScore, "UTF-8");
            scores = scores + "/" + data;
            zooKeeper.setData(path, scores.getBytes(), zooKeeper.exists(path, true).getVersion());
        }
    }


    public static void updateMaster(String user, String score) throws UnsupportedEncodingException, KeeperException, InterruptedException {
        byte[] readMaster = read(MASTER);
        String scores = new String(readMaster, "UTF-8");
        String[] playerList = scores.split("#");
        scores = playerList[0] + "/" + user + ":" + score;
        if(playerList.length > 1){
            if(!playerList[1].contains(user)) {
                scores = scores + "#" + playerList[1] + "," + user;
            }
        }else {
            scores = scores + "#" + user;
        }
        zooKeeper.setData(MASTER, scores.getBytes(), zooKeeper.exists(MASTER, true).getVersion());
    }
}

class ExitPlayer extends Thread {
    String name;
    public static ZooKeeper zooKeeper;
    ExitPlayer(String name){
        this.name = name;
    }
    @Override
    public void run() {
        try {
            String path = MASTER + "/" + name;
            zooKeeper.delete(path, zooKeeper.exists(path, true).getVersion());
            updateMater(name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void updateMater(String name) throws UnsupportedEncodingException, KeeperException, InterruptedException {
        byte[] readMaster = zooKeeper.getData(MASTER, true, zooKeeper.exists(MASTER, true));
        String scores = new String(readMaster, "UTF-8");
        String[] players =  scores.split("#");
        String allLive = "";
        if(players.length > 1){
            if(players[1].contains(name)){
                String[] list = players[1].split(",");
                for(String str : list){
                    if(!str.equals(name)){
                        allLive = allLive + "," +str;
                    }
                }
            }
        }
        System.out.println(allLive);
        String updatedMaster = players[0] + "#" + allLive;
        zooKeeper.setData(MASTER, updatedMaster.getBytes(), zooKeeper.exists(MASTER, true).getVersion());
    }
}
