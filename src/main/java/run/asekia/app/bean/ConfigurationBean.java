package run.asekia.app.bean;

import run.asekia.app.annotation.bean.Bean;
import run.asekia.app.core.PropertiesContext;

import java.util.Properties;

/**
 * 配置文件bean
 * @author Dioxide.CN
 * @date 2023/1/16 17:48
 * @since 1.2
 */
@Bean
public class ConfigurationBean {
    public Properties properties = PropertiesContext.use().properties;
    public String workDir = PropertiesContext.use().workDir;
}
