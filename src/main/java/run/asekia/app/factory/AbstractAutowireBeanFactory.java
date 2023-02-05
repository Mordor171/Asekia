package run.asekia.app.factory;

import run.asekia.app.annotation.bean.Autowired;
import run.asekia.app.exception.AutowiredFailException;
import run.asekia.app.extension.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dioxide.CN
 * @date 2023/1/17 12:06
 * @since 1.3
 */
public class AbstractAutowireBeanFactory extends AbstractBeanFactory {

    @Override
    public HashMap<String, Object> create() {
        AtomicInteger count = new AtomicInteger();
        beanContainer.forEach((name, bean) -> {
            Field[] fields = bean.getClass().getDeclaredFields();

            for (Field field : fields) {
                // 注入被@Autowired修饰的变量
                Autowired autowired = field.getDeclaredAnnotation(Autowired.class);
                if (autowired == null)
                    continue;

                String injectName = autowired.alias().isEmpty() ? field.getType().getSimpleName() : autowired.alias();
                Object instance = beanContainer.get(injectName);

                // 不注入 static 修饰的
                if (Modifier.isStatic(field.getModifiers())) {
                    Log.logger.error("cannot autowired static field " + field.getName());
                    continue;
                }

                // 同步锁注入
                try {
                    synchronized (bean.getClass().getDeclaredField(field.getName())) {
                        field.setAccessible(true);
                        Object ini = field.get(bean);
                        if (ini != null)
                            throw new AutowiredFailException(field.getName() + " cannot be initialized");

                        field.set(bean, instance);
                        count.getAndIncrement();
                    }
                } catch (IllegalAccessException | AutowiredFailException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Log.logger.info("successfully autowired " + count.get() + " beans");
        return null;
    }

    protected volatile static AbstractAutowireBeanFactory INSTANCE = null;
    protected AbstractAutowireBeanFactory() {}

    public static AbstractAutowireBeanFactory use() {
        if (INSTANCE == null) {
            synchronized (AbstractAutowireBeanFactory.class) {
                if (INSTANCE == null) INSTANCE = new AbstractAutowireBeanFactory();
            }
        }
        return INSTANCE;
    }

}
