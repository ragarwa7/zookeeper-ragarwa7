package com.zookeeper.app;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZooKConnector {
    private ZooKeeper zooKeeper;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public ZooKeeper connect(String host) throws IOException, InterruptedException, IllegalStateException {
        zooKeeper = new ZooKeeper(host, 5000,new Watcher(){
            public void process(WatchedEvent event){
                if (Event.KeeperState.SyncConnected == event.getState()) {
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
        return zooKeeper;
    }

    public void close() throws InterruptedException{
        zooKeeper.close();
    }
}
