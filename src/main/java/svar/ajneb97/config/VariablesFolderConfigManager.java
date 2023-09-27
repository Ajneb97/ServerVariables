package svar.ajneb97.config;


import svar.ajneb97.ServerVariables;
import svar.ajneb97.utils.OtherUtils;

import java.io.File;
import java.util.ArrayList;

public class VariablesFolderConfigManager {

    protected ArrayList<CustomConfig> configs;
    protected ServerVariables plugin;
    private String folderName;

    public VariablesFolderConfigManager(ServerVariables plugin, String folderName) {
        this.plugin = plugin;
        this.folderName = folderName;
        this.configs = new ArrayList<CustomConfig>();
    }

    public void configure() {
        createFolder();
        reloadConfigs();
    }

    public void reloadConfigs(){
        this.configs = new ArrayList<CustomConfig>();
        registerConfigs();
    }

    public void createFolder(){
        File folder;
        try {
            folder = new File(plugin.getDataFolder() + File.separator + folderName);
            if(!folder.exists()){
                folder.mkdirs();
                createExample();
            }
        } catch(SecurityException e) {
            folder = null;
        }
    }

    public void createExample(){
        String pathName = "more_variables.yml";
        CustomConfig config = new CustomConfig(pathName,plugin,folderName);
        config.registerConfig();
    }

    public void saveConfigs() {
        for(int i=0;i<configs.size();i++) {
            configs.get(i).saveConfig();
        }
    }

    public void registerConfigs(){
        String path = plugin.getDataFolder() + File.separator + folderName;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (int i=0;i<listOfFiles.length;i++) {
            if(listOfFiles[i].isFile()) {
                String pathName = listOfFiles[i].getName();
                String ext = OtherUtils.getFileExtension(pathName);
                if(!ext.equals("yml")) {
                    continue;
                }
                CustomConfig config = new CustomConfig(pathName, plugin, folderName);
                config.registerConfig();
                configs.add(config);
            }
        }
    }

    public ArrayList<CustomConfig> getConfigs(){
        return this.configs;
    }

    public boolean fileAlreadyRegistered(String pathName) {
        for(int i=0;i<configs.size();i++) {
            if(configs.get(i).getPath().equals(pathName)) {
                return true;
            }
        }
        return false;
    }

    public CustomConfig getConfig(String pathName) {
        for(int i=0;i<configs.size();i++) {
            if(configs.get(i).getPath().equals(pathName)) {
                return configs.get(i);
            }
        }
        return null;
    }

    public boolean registerConfig(String pathName) {
        if(!fileAlreadyRegistered(pathName)) {
            CustomConfig config = new CustomConfig(pathName, plugin, folderName);
            config.registerConfig();
            configs.add(config);
            return true;
        }else {
            return false;
        }
    }

}
