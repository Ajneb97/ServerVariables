package svar.ajneb97.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.config.model.CommonConfig;
import svar.ajneb97.managers.ServerVariablesManager;
import svar.ajneb97.model.ServerVariablesListVariable;
import svar.ajneb97.model.ServerVariablesStringVariable;
import svar.ajneb97.model.ServerVariablesVariable;

public class DataConfigManager {

	private ServerVariables plugin;
	private CommonConfig configFile;
	
	public DataConfigManager(ServerVariables plugin) {
		this.plugin = plugin;
		this.configFile = new CommonConfig("data.yml",plugin,null,false);
		configFile.registerConfig();
	}
	
	public void configure() {
		ServerVariablesManager serverVariablesManager = plugin.getServerVariablesManager();
		FileConfiguration dataFile = configFile.getConfig();

		Map<String,ServerVariablesVariable> variables = new HashMap<>();
		if(dataFile.contains("variables")) {
			for(String key : dataFile.getConfigurationSection("variables").getKeys(false)){
				if(dataFile.isList("variables."+key)){
					variables.put(key,new ServerVariablesListVariable(key, dataFile.getStringList("variables." + key)));
				}else{
					variables.put(key,new ServerVariablesStringVariable(key, dataFile.getString("variables." + key)));
				}
			}
		}
		serverVariablesManager.setVariables(variables);
	}

	public void saveData(){
		ServerVariablesManager serverVariablesManager = plugin.getServerVariablesManager();
		Map<String,ServerVariablesVariable> variables = serverVariablesManager.getVariables();
		FileConfiguration dataFile = configFile.getConfig();
		dataFile.set("variables", null);
		for(Map.Entry<String, ServerVariablesVariable> entry : variables.entrySet()){
			String variableName = entry.getKey();
			dataFile.set("variables."+variableName,entry.getValue().getCurrentValue()); // String or List<String>
		}
		configFile.saveConfig();
	}
}
