package basic.strongseparate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.LinkedBlockingQueue;

public class BossGroup extends ThreadGroup {

    public ThreadGroup getWorkers() {
        return workers;
    }

    public void setWorkers(ThreadGroup workers) {
        this.workers = workers;
    }

    private ThreadGroup workers;

    public BossGroup(int threadNums) {
        super(threadNums);
        BossHandler handler = new BossHandler();
        for(int i = 0; i < threadNums; i ++) {
            threads[i] = new MyThread(handler);
            threads[i].start();
        }
    }

    public void bind(int port) {
        try {
            ServerSocketChannel server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(port));
            server.configureBlocking(false);
            register(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class BossHandler implements MyHandler {

        @Override
        public void process(SelectionKey key) {
            try {
                ServerSocketChannel server = (ServerSocketChannel) key.channel();
                SocketChannel client = server.accept();
                client.configureBlocking(false);
                ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                workers.register(client);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void processQueue(LinkedBlockingQueue<Channel> queue, Selector selector) {
            try {
                while (!queue.isEmpty()) {
                    Channel chan = queue.take();
                    ServerSocketChannel server = (ServerSocketChannel) chan;
                    server.configureBlocking(false);
                    server.register(selector, SelectionKey.OP_ACCEPT);
                    System.out.println(Thread.currentThread().getName() + "开启服务端口监听---" + server.getLocalAddress());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
