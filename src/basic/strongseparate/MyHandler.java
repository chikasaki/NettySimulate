package basic.strongseparate;

import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.LinkedBlockingQueue;

public interface MyHandler {
    void process(SelectionKey key);
    void processQueue(LinkedBlockingQueue<Channel> queue, Selector selector);
}
