package basic.strongseparate;

public class Main {
    public static void main(String[] args) {
        BossGroup bossGroup = new BossGroup(1);
        WorkerGroup workerGroup = new WorkerGroup(1);
        bossGroup.setWorkers(workerGroup);

        bossGroup.bind(9999);
//        bossGroup.bind(7777);
//        bossGroup.bind(6666);
//        bossGroup.bind(8888);
    }
}
