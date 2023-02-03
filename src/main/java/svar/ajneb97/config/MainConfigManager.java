package svar.ajneb97.config;

import org.bukkit.configuration.file.FileConfiguration;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.managers.MessagesManager;
import svar.ajneb97.model.structure.ValueType;
import svar.ajneb97.model.structure.Variable;
import svar.ajneb97.model.structure.VariableType;
import svar.ajneb97.tasks.DataSaveTask;

import java.util.ArrayList;
import java.util.List;

public class MainConfigManager {

	private ServerVariables plugin;
	private CustomConfig configFile;

	public MainConfigManager(ServerVariables plugin) {
		this.plugin = plugin;
		this.configFile = new CustomConfig("config.yml",plugin,null);
		configFile.registerConfig();
	}

	public void reload(){
		configFile.reloadConfig();
		configure();
	}
	
	public void configure() {
		FileConfiguration config = configFile.getConfig();
		ArrayList<Variable> variables = new ArrayList<>();

		if(config.contains("variables")){
			for(String key : config.getConfigurationSection("variables").getKeys(false)){
				String path = "variables."+key;
				VariableType variableType = VariableType.valueOf(config.getString(path+".variable_type"));
				ValueType valueType = ValueType.valueOf(config.getString(path+".value_type"));
				String initialValue = config.getString(path+".initial_value");

				List<String> possibleValues = new ArrayList<String>();
				if(config.contains(path+".possible_values")){
					possibleValues = config.getStringList(path+".possible_values");
				}


				Variable variable = new Variable(key, variableType, valueType, initialValue, possibleValues);
				variables.add(variable);
			}
		}

		plugin.getVariablesManager().setVariables(variables);
		plugin.setMessagesManager(new MessagesManager(config.getString("messages.prefix")));

		DataSaveTask dataSaveTask = plugin.getDataSaveTask();
		if(dataSaveTask != null) {
			dataSaveTask.end();
		}
		dataSaveTask = new DataSaveTask(plugin);
		dataSaveTask.start(config.getInt("config.data_save_time"));
	}
}
