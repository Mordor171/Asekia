package run.asekia.app.factory;

import run.asekia.app.annotation.bean.Configuration;
import run.asekia.app.exception.ConfigurationCreateException;
import run.asekia.app.extension.Log;
import run.asekia.app.extension.gc.UselessInstanceGC;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * @author Dioxide.CN
 * @date 2023/1/17 14:44
 * @since 1.3
 */
public class ConfigurationFactory extends AbstractBeanFactory {
    private final HashMap<String, Object> configContainer = new HashMap<>();
    private final ReflectFactory reflectFactory = ReflectFactory.use();

    @Override
    public HashMap<String, Object> create() {
        return this.createConfig();
    }

    private HashMap<String, Object> createConfig() {
        return findConfig();
    }

    private HashMap<String, Object> findConfig() {
        for (Class<?> waitingConfigClass : reflectFactory.getAllClass()) {
            if (waitingConfigClass.getDeclaredAnnotation(Configuration.class) == null)
                continue;
            resolveParentalDelegation(waitingConfigClass);
        }
        UselessInstanceGC.collect(reflectFactory); // leave to g1
        return configContainer;
    }

    private void resolveParentalDelegation(Class<?> sourceConfig) {
        Object existConfig = configContainer.get(sourceConfig.getSimpleName());
        Object superConfig = configContainer.get(sourceConfig.getSuperclass().getSimpleName());

        try {
            Constructor<?> configConstructor = sourceConfig.getDeclaredConstructor();
            configConstructor.setAccessible(true);
            // 对Configuration双亲委派
            if (existConfig == null && superConfig == null) {
                // 本身不存在且父类不存在则构造本身
                configContainer.put(sourceConfig.getSimpleName(), configConstructor.newInstance());
            } else if (existConfig == null) {
                // 本身不存在但父类存在对父类进行覆盖
                Configuration superAnno = superConfig.getClass().getDeclaredAnnotation(Configuration.class);
                // 判断父类是否能重写
                if (superAnno != null && !superAnno.extendable())
                    throw new ConfigurationCreateException("configuration " + sourceConfig.getSimpleName() + "'s super class cannot rewrite");
                configContainer.put(superConfig.getClass().getSimpleName(), configConstructor.newInstance());
            } else {
                // 本身存在且父类存再覆盖就抛出异常
                throw new ConfigurationCreateException("configuration " + sourceConfig.getSimpleName() + " has created");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected volatile static ConfigurationFactory INSTANCE = null;
    protected ConfigurationFactory() {}

    public static ConfigurationFactory use() {
        if (INSTANCE == null) {
            synchronized (ConfigurationFactory.class) {
                if (INSTANCE == null) INSTANCE = new ConfigurationFactory();
            }
        }
        return INSTANCE;
    }

    public void log() {
        Log.logger.info("successfully detected " + configContainer.size() + " configurations");
    }
}
