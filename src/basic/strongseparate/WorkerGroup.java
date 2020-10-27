package basic.strongseparate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.LinkedBlockingQueue;

public class WorkerGroup extends ThreadGroup {
    public WorkerGroup(int threadNums) {
        super(threadNums);
        WorkerHandler handler = new WorkerHandler();
        for(int i = 0; i < threadNums; i ++) {
            threads[i] = new MyThread(handler);
            threads[i].start();
        }
    }

    private class WorkerHandler implements MyHandler {

        @Override
        public void process(SelectionKey key) {
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buffer = (ByteBuffer) key.attachment();
            buffer.clear();
            while(true) {
                try {
                    int read = client.read(buffer);
                    if(read > 0) {
                        buffer.flip();
                        while(buffer.hasRemaining()) {
                            client.write(buffer);
                        }
                        buffer.clear();
                    } else if(read == 0) {
                        break;
                    } else {
                        key.cancel();
                        client.close();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void processQueue(LinkedBlockingQueue<Channel> queue, Selector selector) {
            while(!queue.isEmpty()) {
                try {
                    Channel channel = queue.take();
                    SocketChannel client = (SocketChannel) channel;
                    ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                    client.register(selector, SelectionKey.OP_READ, buffer);
                    System.out.println(Thread.currentThread().getName() + "开启远程读写事件监听---" + client.getRemoteAddress());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
