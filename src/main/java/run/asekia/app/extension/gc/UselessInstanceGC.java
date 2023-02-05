package run.asekia.app.extension.gc;

/**
 * @author Dioxide.CN
 * @date 2023/1/17 14:27
 * @since 1.3
 */
public class UselessInstanceGC {
    public static void collect(Object ...garbage) {
        System.gc();
    }
}
