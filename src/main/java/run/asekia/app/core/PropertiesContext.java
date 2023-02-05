package run.asekia.app.core;

import run.asekia.app.extension.Log;

import java.io.*;
import java.util.Properties;

/**
 * @author Dioxide.CN
 * @date 2023/1/16 16:36
 * @since 1.2
 */
public class PropertiesContext {
    protected InputStream bannerFile = PropertiesContext.class.getClassLoader().getResourceAsStream("banner.txt");
    public String workDir = "";
    public Properties properties = new Properties();

    protected void loadWorkDirectory() {
        File workFile = new File("");
        workFile = new File(workFile.getAbsolutePath() + "\\config");
        // 没有 config 目录则创建目录
        if (!workFile.exists()) {
            boolean mkdir = workFile.mkdir();
            if (!mkdir) {
                Log.logger.error("can't create directory 'config'");
            }
        }

        workFile = new File(workFile.getAbsolutePath() + "\\asekia.properties");
        if (!workFile.exists()) {
            boolean newFile = false;
            try {
                newFile = workFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!newFile) {
                Log.logger.error("can't create file 'asekia.properties'");
            }
        }

        // 拿到 properties
        properties = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(workFile);
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        workDir = properties.getProperty("application.workDir");
        if (workDir == null || workDir.isEmpty()) {
            // 在 config 目录下工作否则用配置的
            workDir = new File("").getAbsolutePath() + "\\config";
        }

        try {
            loadFileAndConfig(); // 装配文件
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void loadFileAndConfig() throws IOException {
        // 装配工作目录下的文件
        File waitingBannerFile = new File(workDir + "\\banner.txt");
        if (waitingBannerFile.exists())
            bannerFile = new FileInputStream(waitingBannerFile);

        File propertyFile = new File(workDir + "\\asekia.properties");
        if (!propertyFile.exists()) {
            Log.logger.error("file asekia.properties doesn't exist");
            throw new IOException();
        }

        InputStream input = new FileInputStream(propertyFile);
        properties.load(input);
    }

    protected volatile static PropertiesContext INSTANCE = null;
    protected PropertiesContext() {}

    public static PropertiesContext use() {
        if (INSTANCE == null) {
            synchronized (PropertiesContext.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PropertiesContext();
                }
            }
        }
        return INSTANCE;
    }
}
