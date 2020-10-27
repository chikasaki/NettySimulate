package basic.strongseparate;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class MyThread extends Thread {
    private Selector selector;
    private LinkedBlockingQueue<Channel> queue;
    private MyHandler handler;

    public MyThread(MyHandler handler) {
        try {
            selector = Selector.open();
            queue = new LinkedBlockingQueue<>();
            this.handler = handler;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void wakeUp() {
        this.selector.wakeup();
    }

    public void put(Channel channel) {
        try {
            queue.put(channel);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
//                System.out.println(Thread.currentThread().getName() + "before-selector: " + selector.keys().size());
                int num = selector.select();
//                System.out.println(Thread.currentThread().getName() + "after-selector: " + selector.keys().size());
                if (num > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        handler.process(key);
                    }
                }

                handler.processQueue(queue, selector);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
