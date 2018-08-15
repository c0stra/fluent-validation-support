package fluent.validation.utils;

public class Async {

    public static void async(Runnable runnable) {
        new Thread(runnable).start();
    }

}
