package svar.ajneb97.config;


import svar.ajneb97.ServerVariables;
import svar.ajneb97.config.model.CommonConfig;

import java.io.File;
import java.util.ArrayList;

@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class DataFolderConfigManager {

    protected String folderName;
    protected ServerVariables plugin;

    public DataFolderConfigManager(ServerVariables plugin, String folderName) {
        this.plugin = plugin;
        this.folderName = folderName;
    }

    public void configure() {
        createFolder();
        loadConfigs();
    }

    public void createFolder() {
        File folder;
        try {
            folder = new File(plugin.getDataFolder() + File.separator + folderName);
            if (!folder.exists()) {
                folder.mkdirs();
                createFiles();
            }
        } catch (SecurityException ignored) {
        }
    }

    public CommonConfig getConfigFile(String pathName) {
        CommonConfig commonConfig = new CommonConfig(pathName, plugin, folderName, true);
        commonConfig.registerConfig();
        return commonConfig;
    }

    public ArrayList<CommonConfig> getConfigs() {
        ArrayList<CommonConfig> configs = new ArrayList<>();

        String pathFile = plugin.getDataFolder() + File.separator + folderName;
        File folder = new File(pathFile);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return configs;
        }

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String pathName = file.getName();
                CommonConfig commonConfig = new CommonConfig(pathName, plugin, folderName, true);
                commonConfig.registerConfig();
                configs.add(commonConfig);
            }
        }

        return configs;
    }

    public abstract void createFiles();

    public abstract void loadConfigs();

    public abstract void saveConfigs();
}
