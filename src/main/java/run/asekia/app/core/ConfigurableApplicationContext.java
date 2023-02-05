package run.asekia.app.core;

import run.asekia.app.AsekiaApplication;
import run.asekia.app.bean.ConfigurationBean;
import run.asekia.app.extension.Log;
import run.asekia.app.factory.AbstractBeanFactory;

import java.util.Arrays;

/**
 * @author Dioxide.CN
 * @date 2023/1/16 9:36
 * @since 1.0
 */
public class ConfigurableApplicationContext {
    private final long begin = System.currentTimeMillis();

    /**
     * start主要负责装配文件、初始化BeanFactory
     * @param primarySource 启动主类
     * @param args jvm参数
     * @return AsekiaApplication类
     */
    public ConfigurableApplicationContext start(Class<?> primarySource, String... args) {
        FactoryPipelineContext.use().start(primarySource); // 构建流水线完成自动配置
        this.logger(primarySource);                        // 输出日志
        return AsekiaApplication.app();                    // 返回程序
    }

    private void logger(Class<?> primarySource) {
        Log.logger.info("application is running in " + ((ConfigurationBean) getBean("ConfigurationBean")).workDir);
        Log.logger.info("started " + primarySource.getSimpleName() + " in " + (System.currentTimeMillis()-begin) + " ms");
    }

    protected ConfigurableApplicationContext() {}

    /**
     * 依据注册的beanName获取Bean实例
     * @param beanName 注册的beanName
     * @return Bean实例
     */
    public Object getBean(String beanName) {
        return AbstractBeanFactory.use().beanContainer.get(beanName);
    }

    /**
     * 依据注册beanName获取clazz类型的Bean实例
     * @param beanName 注册的beanName
     * @param clazz 强制转换的目标类型
     * @return Bean实例
     */
    public Object getBean(String beanName, Class<?> clazz) {
        return clazz.cast(AbstractBeanFactory.use().beanContainer.get(beanName));
    }

    /**
     * 获取所有注册的Bean实例
     * @return 所有Bean实例数组
     */
    public Object[] getAllBean() {
        return AbstractBeanFactory.use().beanContainer.values().toArray();
    }
}
