package basic.separate;

public class Main {
    public static void main(String[] args) {
        ThreadGroup bossGroup = new ThreadGroup(3);
        ThreadGroup workerGroup = new ThreadGroup(3);
        bossGroup.setWorkers(workerGroup);
        bossGroup.bind(9999);
        bossGroup.bind(8888);
        bossGroup.bind(7777);
        bossGroup.bind(6666);
    }
}
