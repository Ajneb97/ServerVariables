package svar.ajneb97.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.managers.ServerVariablesManager;
import svar.ajneb97.model.ServerVariablesVariable;

public class DataConfigManager {

	private ServerVariables plugin;
	private CustomConfig configFile;
	
	public DataConfigManager(ServerVariables plugin) {
		this.plugin = plugin;
		this.configFile = new CustomConfig("data.yml",plugin,null);
		configFile.registerConfig();
	}
	
	public void configure() {
		ServerVariablesManager serverVariablesManager = plugin.getServerVariablesManager();
		serverVariablesManager.clearVariables();
		FileConfiguration dataFile = configFile.getConfig();

		if(dataFile.contains("variables")) {
			for(String key : dataFile.getConfigurationSection("variables").getKeys(false)){
				serverVariablesManager.addVariable(key,dataFile.getString("variables."+key));
			}
		}
	}

	public void saveData(){
		ServerVariablesManager serverVariablesManager = plugin.getServerVariablesManager();
		ArrayList<ServerVariablesVariable> variables = serverVariablesManager.getVariables();
		FileConfiguration dataFile = configFile.getConfig();
		dataFile.set("variables", null);
		for(ServerVariablesVariable v : variables){
			String variableName = v.getVariableName();
			dataFile.set("variables."+variableName,v.getCurrentValue());
		}
		configFile.saveConfig();
	}
}
