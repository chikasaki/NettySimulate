package basic.separate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class SelectionThread extends Thread {

    private Selector selector;
    private LinkedBlockingQueue<Channel> queue;
    private ThreadGroup group;

    public SelectionThread(ThreadGroup group) {
        try {
            selector = Selector.open();
            queue = new LinkedBlockingQueue<>();
            this.group = group;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean put(Channel c) {
        try {
            queue.put(c);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void wakeUp() {
        this.selector.wakeup();
    }

    @Override
    public void run() {
        while(true) {
            try {
                int num = selector.select();
                if (num > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isAcceptable()) {
                            acceptHandler(key);
                        } else if (key.isReadable()) {
                            readHandler(key);
                        } else if (key.isWritable()) {

                        }
                    }
                }

                // 看看是否有新的事件需要注册
                while (!queue.isEmpty()) {
                    try {
                        Channel channel = queue.take();
                        if (channel instanceof ServerSocketChannel) {
                            ServerSocketChannel server = (ServerSocketChannel) channel;
                            server.register(selector, SelectionKey.OP_ACCEPT);
                            System.out.println(Thread.currentThread().getName() + ": 开启服务--" + server.getLocalAddress());
                        } else if (channel instanceof SocketChannel) {
                            SocketChannel client = (SocketChannel) channel;
                            ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                            client.register(selector, SelectionKey.OP_READ, buffer);
                            System.out.println(Thread.currentThread().getName() + ": 注册事件--" + client.getRemoteAddress());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void acceptHandler(SelectionKey key) {
        try {
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            SocketChannel client = channel.accept();
            client.configureBlocking(false);
            group.getWorkers().register(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHandler(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.clear();
        try {
            int read = 0;
            while(true) {
                read = channel.read(buffer);
                if(read > 0) {
                    buffer.flip();
                    while(buffer.hasRemaining()) {
                        channel.write(buffer);
                    }
                    buffer.clear();
                } else if(read == 0) {
                    break;
                } else {
                    key.cancel();
                    channel.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
