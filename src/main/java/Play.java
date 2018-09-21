
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class Play {

    public static ZooKeeper zooKeeper;
    public static ZooKConnector zooKConnector;
    public static final String MASTER = "/watch";
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


    public void create(String name, int count, int delay, int score) throws KeeperException, InterruptedException, UnsupportedEncodingException {
        String path = MASTER + "/" + name;
        if(zooKeeper.exists(path, true) != null){
            System.out.println("Player already exist");
            System.exit(0);
        }else {
            int n = 0;
            Random random = new Random();
            double delaySd = 1.5;
            double scoreSd = 2;
            double delays = delay;
            zooKeeper.create(path, String.valueOf(score).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            while(n < count){
                TimeUnit.SECONDS.sleep((long) delays);
                delays = random.nextGaussian() * delaySd + delay;
                int newScore = (int) (random.nextGaussian() * scoreSd + score);
                System.out.println("Adding new score:" + newScore);
                update(path, String.valueOf(newScore));
                updateMaster(name, String.valueOf(newScore));
                n++;
            }
            ExitPlayer.zooKeeper = zooKeeper;
            ExitPlayer exitPlayer = new ExitPlayer(name);
            exitPlayer.updateMater(name);
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
        String masterScore = new String(readMaster, "UTF-8");
        String[] playerList = masterScore.split("#");
        String scores = playerList[0] + "/" + user + ":" + score;
        String finalscores = "";
        if(playerList.length > 1){
            String[] list = playerList[1].split(",");
            List<String> list1 = Arrays.asList(list);
            if(!list1.contains(user)) {
                finalscores = scores + "#" + playerList[1] + "," + user;
            }else{
                finalscores = scores + "#" + playerList[1];
            }
        }else {
            finalscores = scores + "#" + user;
        }
        zooKeeper.setData(MASTER, finalscores.getBytes(), zooKeeper.exists(MASTER, true).getVersion());
    }
}

class ExitPlayer extends Thread {

    public static final String MASTER = "/watch";
    String name;
    public static ZooKeeper zooKeeper;
    ExitPlayer(String name){
        this.name = name;
    }
    @Override
    public void run() {
        try {
            updateMater(name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void updateMater(String name) throws UnsupportedEncodingException, KeeperException, InterruptedException {
        String path = MASTER + "/" + name;
        zooKeeper.delete(path, zooKeeper.exists(path, true).getVersion());
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
        String updatedMaster = players[0] + "#" + allLive;
        zooKeeper.setData(MASTER, updatedMaster.getBytes(), zooKeeper.exists(MASTER, true).getVersion());
    }
}
