package run.asekia.app.factory;

import run.asekia.app.annotation.bean.Bean;
import run.asekia.app.annotation.bean.Component;
import run.asekia.app.annotation.bean.Service;
import run.asekia.app.extension.Reflect;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>反射工厂主要集中处理具体包下的类文件，通过反射工厂可以拿到所有类文件。反射工厂先于所有工厂被使用和创建。</p>
 * @author Dioxide.CN
 * @date 2023/1/17 12:13
 * @since 1.3
 */
public class ReflectFactory {

    private final Reflect reflect = new Reflect();

    private final Set<Class<?>> sourceClassSet = new HashSet<>();
    private final Set<Class<?>> beanAnnoHashSet = new HashSet<>();

    public void scanAllPackage(Class<?> primarySourceClass) {
        // 装载所有Class文件到该集合中
        sourceClassSet.addAll(reflect.getClasses("run.asekia.app"));
        sourceClassSet.addAll(reflect.getClasses(primarySourceClass.getPackage().getName()));

        // 处理Bean类注解Class
        resolveBeenAnnotations();
    }

    public void resolveBeenAnnotations() {
        beanAnnoHashSet.add(Bean.class);
        beanAnnoHashSet.add(Component.class);
        beanAnnoHashSet.add(Service.class);
    }

    public Set<Class<?>> getAllClass() {
        return sourceClassSet;
    }

    public Set<Class<?>> getBeanAnnoClass() {
        return beanAnnoHashSet;
    }

    private volatile static ReflectFactory INSTANCE = null;
    private ReflectFactory() {}

    public static ReflectFactory use() {
        if (INSTANCE == null) {
            synchronized (ReflectFactory.class) {
                if (INSTANCE == null) INSTANCE = new ReflectFactory();
            }
        }
        return INSTANCE;
    }

}
