import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class ReadZNode {

    private static ZooKeeper zooKeeper;
    private static ZooKConnector connector;

    public static byte[] read(String path) throws KeeperException, InterruptedException {
        return zooKeeper.getData(path, true, zooKeeper.exists(path, true));
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        String path = "/test3";

        connector = new ZooKConnector();
        zooKeeper = connector.connect("localhost");
        byte[] data  = read(path);

        for(byte byt : data){
            System.out.print((char) byt);
        }
    }
}
