package run.asekia.app.core;

import run.asekia.app.factory.AbstractAutowireBeanFactory;
import run.asekia.app.factory.AbstractBeanFactory;
import run.asekia.app.factory.ConfigurationFactory;
import run.asekia.app.factory.ReflectFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.lang.System.out;

/**
 * @author Dioxide.CN
 * @date 2023/1/17 14:34
 * @since 1.3
 */
public class FactoryPipelineContext {

    private static final PropertiesContext propertiesContext = PropertiesContext.use();

    public void start(Class<?> primarySource) {
        propertiesContext.loadWorkDirectory();              // 装配文件
        ReflectFactory.use().scanAllPackage(primarySource); // 反射工厂
        ConfigurationFactory.use().create();                // 配置工厂
        this.banner();                                      // 输出Banner
        ConfigurationFactory.use().log();                   // 配置工厂日志
        AbstractBeanFactory.use().create();                 // Bean工厂
        AbstractAutowireBeanFactory.use().create();         // 自动装配
        // 代理工厂
    }

    private volatile static FactoryPipelineContext INSTANCE = null;
    private FactoryPipelineContext() {}

    public static FactoryPipelineContext use() {
        if (INSTANCE == null) {
            synchronized (FactoryPipelineContext.class) {
                if (INSTANCE == null) INSTANCE = new FactoryPipelineContext();
            }
        }
        return INSTANCE;
    }

    private void banner() {
        InputStream input = propertiesContext.bannerFile;

        assert input != null;
        InputStreamReader reader = new InputStreamReader(input);
        BufferedReader buffReader = new BufferedReader(reader);
        StringBuilder builder = new StringBuilder("\n");
        String line;
        while(true) {
            try {
                if ((line = buffReader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            builder.append(line).append("\n");
        }
        String bannerTxt = builder.toString()
                .replaceAll("\\$\\{Color.BLUE}", "\033[34m")
                .replaceAll("\\$\\{Color.GREEN}", "\033[32m")
                .replaceAll("\\$\\{Color.CYAN}", "\033[36m")
                .replaceAll("\\$\\{Color.YELLOW}", "\033[33m");
        out.println(bannerTxt + "\033[38m");
        try {
            buffReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
