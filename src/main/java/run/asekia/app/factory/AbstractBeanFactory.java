package run.asekia.app.factory;

import run.asekia.app.annotation.bean.Bean;
import run.asekia.app.exception.BeanCreateException;
import run.asekia.app.extension.Log;
import run.asekia.app.extension.gc.UselessInstanceGC;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * <p>抽象Bean工厂主要负责对 {@code @Bean @Component @Service} 类型的单例Bean通过控制反转实例化到公共Bean容器中。</p>
 * <p>Bean工厂实例化后于Configuration工厂实例化，Bean工厂实例化完成后会交付给 {@link AbstractAutowireBeanFactory} 工厂完成依赖注入。</p>
 * @author Dioxide.CN
 * @date 2023/1/17 12:06
 * @since 1.3
 */
public class AbstractBeanFactory {
    public final HashMap<String, Object> beanContainer = new HashMap<>();
    private final HashMap<Class<?>, Annotation> handlingMap = new HashMap<>();
    private final HashMap<String, Class<?>> initializeMap = new HashMap<>();
    private final ReflectFactory reflectFactory = ReflectFactory.use();

    public HashMap<String, Object> create() {
        return this.createBean();
    }

    // 初始化Bean实例
    private void initializeBean() {
        initializeMap.forEach((beanName, beanClass) -> {
            try {
                Constructor<?> beanConstructor = beanClass.getDeclaredConstructor();
                beanConstructor.setAccessible(true);
                Object beanInstance = beanConstructor.newInstance();
                beanContainer.put(beanName, beanInstance);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 销毁Bean实例
    private void destroyBean(Object existingBean) {

    }

    // 解决Bean实例的别名
    private AbstractBeanFactory resolveAlias() {
        handlingMap.forEach((beanClass, anno) -> {
            // 此时集合中都是待处理的Bean类
            Annotation beanAnno = beanClass.getDeclaredAnnotation(anno.annotationType());
            assert beanAnno != null;
            try {
                // 强转为Bean进行注入并校验是否重复
                duplicateBeanResolver(((Bean) beanAnno).alias(), beanClass, initializeMap);
            } catch (BeanCreateException e) {
                throw new RuntimeException(e);
            }
        });
        return this;
    }

    /**
     * 查找所有合法Bean类载入待处理集合handlingBeanContainer中
     * @return 当前工厂
     */
    private AbstractBeanFactory findBean() {
        for (Class<?> singleClass : reflectFactory.getAllClass()) {
            // 解决每个class的注解类型
            try {
                Annotation annoResult = resolveBeanAnno(singleClass.getDeclaredAnnotations());
                if (annoResult == null) continue;
                // 将包含Bean类型注解的类的注解类型和类本身存入待处理集合
                handlingMap.put(singleClass, annoResult);
            } catch (BeanCreateException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    /**
     * 处理传递过来的类上注解数组判断是否包含Bean类型的注解并返回他的注解类型
     * @param annoHeadArray 类上的Bean类型注解数组
     * @return 满足Bean类型注解的注解类型
     * @throws BeanCreateException 类上拥有多个Bean类型的注解
     */
    private Annotation resolveBeanAnno(Annotation[] annoHeadArray) throws BeanCreateException {
        if (annoHeadArray.length == 0) return null;

        boolean canHave = true;
        Annotation result = null;
        for (Class<?> existingBeanAnnoClass : reflectFactory.getBeanAnnoClass()) {
            for (Annotation anno : annoHeadArray) {
                // 在annoHeadArray中寻找是否有满足existingBeanAnnoClass类型的注解
                if (anno.annotationType().getSimpleName().equals(existingBeanAnnoClass.getSimpleName())) {
                    if (canHave) {
                        result = anno;
                        canHave = false;
                    } else {
                        // 发现多个Bean类型的注解
                        throw new BeanCreateException("class " + existingBeanAnnoClass.getName() + " has too many bean type");
                    }
                }
            }
        }
        return result;
    }

    private void duplicateBeanResolver(String name, Class<?> beanClass, HashMap<String, Class<?>> beanMap) throws BeanCreateException {
        String beanName = name.isEmpty() ? beanClass.getSimpleName() : name;
        if (beanMap.get(beanName) != null)
            throw new BeanCreateException("find duplicate bean " + beanClass.getName());

        beanMap.put(beanName, beanClass);
    }

    private HashMap<String, Object> createBean() {
        this.findBean().resolveAlias().initializeBean();
        UselessInstanceGC.collect(handlingMap, initializeMap, reflectFactory);
        Log.logger.info("successfully detected " + beanContainer.size() + " beans");
        return beanContainer;
    }

    protected volatile static AbstractBeanFactory INSTANCE = null;
    protected AbstractBeanFactory() {}

    public static AbstractBeanFactory use() {
        if (INSTANCE == null) {
            synchronized (AbstractBeanFactory.class) {
                if (INSTANCE == null) INSTANCE = new AbstractBeanFactory();
            }
        }
        return INSTANCE;
    }
}
