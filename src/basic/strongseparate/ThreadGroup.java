package basic.strongseparate;

import java.nio.channels.Channel;

public abstract class ThreadGroup {
    MyThread[] threads;
    int threadNums;
    private int nextThread;

    public ThreadGroup(int threadNums) {
        this.threadNums = threadNums;
        this.threads = new MyThread[threadNums];
        this.nextThread = 0;
    }

    public synchronized int next() {
        int ans = nextThread;
        nextThread = ++nextThread % threadNums;
        return ans;
    }

    public void register(Channel channel) {
        int index = next();
        threads[index].put(channel);
        threads[index].wakeUp();
    }
}
