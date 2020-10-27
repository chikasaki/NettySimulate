package basic.mixed;

public class Main {

    public static void main(String[] args) {
        ThreadGroup group = new ThreadGroup(3);
        group.bind(9999);
    }
}
