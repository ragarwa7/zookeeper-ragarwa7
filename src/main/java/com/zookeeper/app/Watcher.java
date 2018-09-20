package com.zookeeper.app;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Watcher implements org.apache.zookeeper.Watcher {

    private static final String watchedPath = "/ragarwa7";
    private static ZooKeeper zooKeeper;
    private static ZooKConnector zooKConnector;
    private static List<String> livePlayers;

    private static int number_of_records;

    public void process(WatchedEvent watchedEvent) {
        byte[] readMaster;
        try {
            readMaster = zooKeeper.getData(watchedPath, true, zooKeeper.exists(watchedPath, true));
            String scores = new String(readMaster, "UTF-8");
            recentNMatch(scores);
            NHighestScore(scores);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        String command;// = args[0];
        String ipAddress = "localhost"; //args[1];
        number_of_records = 2; //Integer.parseInt(args[2]);
        zooKConnector = new ZooKConnector();
        zooKeeper = zooKConnector.connect(ipAddress);
        Watcher watchPlayers = new Watcher();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            zooKeeper.getData(watchedPath, watchPlayers, zooKeeper.exists(watchedPath, true));
            countDownLatch.await(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
        }catch (Exception e){

        }
    }

    private static void recentNMatch(String scores) throws KeeperException, InterruptedException, UnsupportedEncodingException {
        String[] players = scores.split("#");
        if(players.length > 1) {
            livePlayers = Arrays.asList(players[1].split(","));
        }else {
            livePlayers = new ArrayList<String>();
        }
        String[] recentPlayerList = players[0].split("/");
        int len = recentPlayerList.length - 1;
        System.out.println("Most recent scores");
        System.out.println("-------------------");
        int count = 0;
        while (count < number_of_records){
            if(len < 0) break;
            String[] info = recentPlayerList[len].split(":");
            if(info.length > 1) {
                if(livePlayers.contains(info[0])) {
                    System.out.println(info[0] + "         " + info[1] + "   **");
                }else{
                    System.out.println(info[0] + "         " + info[1]);
                }
                count++;
            }
            len--;
        }
    }

    private static void NHighestScore(String scores) throws KeeperException, InterruptedException, UnsupportedEncodingException {
        String[] players = scores.split("#");
        String[] recentPlayerList = players[0].split("/");
        int len = recentPlayerList.length;
        Map<Integer, ArrayList<String>> map = new HashMap<Integer, ArrayList<String>>();
        for(String score: recentPlayerList){
            String[] info = score.split(":");
            if(info.length > 1) {
                String name = info[0];
                Integer key = Integer.valueOf(info[1]);
                ArrayList<String> list;
                if (map.containsKey(key)) {
                    list = map.get(key);
                } else {
                    list = new ArrayList<String>();
                }
                list.add(name);
                map.put(key, list);
            }
        }
        List<Integer> integers = new ArrayList<Integer>(map.keySet());
        Collections.sort(integers);
        Collections.reverse(integers);
        System.out.println("Highest scores");
        System.out.println("-------------------");
        int count = 0;
        for(Integer integer: integers){
            ArrayList<String> names = map.get(integer);
            for(String name: names){
                if(count < number_of_records) {
                    if (livePlayers.contains(name)) {
                        System.out.println(name + "         " + integer + "   **");
                    } else {
                        System.out.println(name + "         " + integer);
                    }
                }else {
                    break;
                }
                count++;
            }
        }
    }
}
