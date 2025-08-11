package svar.ajneb97.config;


import svar.ajneb97.ServerVariables;
import svar.ajneb97.config.model.CommonConfig;
import svar.ajneb97.utils.OtherUtils;

import java.io.File;
import java.util.ArrayList;

public class VariablesFolderConfigManager extends DataFolderConfigManager{

    public VariablesFolderConfigManager(ServerVariables plugin, String folderName) {
        super(plugin, folderName);
    }

    @Override
    public void createFiles() {
        new CommonConfig("more_variables.yml",plugin,folderName,false).registerConfig();
    }

    @Override
    public void loadConfigs() {

    }

    @Override
    public void saveConfigs() {

    }


}
