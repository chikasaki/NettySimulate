package basic.mixed;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;

public class ThreadGroup {
    private SelectionThread[] threads;
    private int threadNums;
    private int nextThread;

    public ThreadGroup(int threadNums) {
        this.threadNums = threadNums;
        this.threads = new SelectionThread[threadNums];
        for(int i = 0; i < threadNums; i ++) {
            threads[i] = new SelectionThread(this);
            threads[i].start();
        }
        this.nextThread = 0;
    }

    public void bind(int port) {
        try {
            ServerSocketChannel server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(port));
            server.configureBlocking(false);

            //将server注册到线程组某一个线程的selector中去
            register(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(Channel channel) {
        int index = nextThread();
        threads[index].put(channel);
        threads[index].wakeUp();
    }

    private synchronized int nextThread() {
        int ans = nextThread;
        nextThread = ++nextThread % threadNums;
        return ans;
    }
}
