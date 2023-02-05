package run.asekia.app;

import run.asekia.app.core.ConfigurableApplicationContext;

/**
 * @author Dioxide.CN
 * @date 2023/1/16 8:56
 * @since 1.0
 */
public class AsekiaApplication extends ConfigurableApplicationContext {

    public static AsekiaApplication loader(Class<?> primarySource, String... args) {
        return (AsekiaApplication) AsekiaApplication.app().start(primarySource, args);
    }

    public static AsekiaApplication run(Class<?> primarySource, String... args) {
        return loader(primarySource, args);
    }

    private volatile static AsekiaApplication INSTANCE = null;
    private AsekiaApplication() {}

    public static AsekiaApplication app() {
        // 第一次检查单例对象是否已被构建
        if (INSTANCE == null) {
            synchronized (AsekiaApplication.class) {
                // 第二次检查防止二次构造覆盖
                if (INSTANCE == null) {
                    // 非原子操作
                    INSTANCE = new AsekiaApplication();
                }
            }
        }
        return INSTANCE;
    }

}
