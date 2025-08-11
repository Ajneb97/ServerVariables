package svar.ajneb97.config;

import org.bukkit.configuration.file.FileConfiguration;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.config.model.CommonConfig;
import svar.ajneb97.model.structure.Limitations;
import svar.ajneb97.model.structure.ValueType;
import svar.ajneb97.model.structure.Variable;
import svar.ajneb97.model.structure.VariableType;

import java.util.ArrayList;
import java.util.List;

public class ConfigsManager {

	private PlayersConfigsManager playerConfigsManager;
	private DataConfigManager dataConfigManager;
	private MainConfigManager mainConfigManager;
	private VariablesFolderConfigManager variablesFolderConfigManager;
	private ServerVariables plugin;
	
	public ConfigsManager(ServerVariables plugin) {
		this.plugin = plugin;
		this.mainConfigManager = new MainConfigManager(plugin);
		this.playerConfigsManager = new PlayersConfigsManager(plugin,"players");
		this.dataConfigManager = new DataConfigManager(plugin);
		this.variablesFolderConfigManager = new VariablesFolderConfigManager(plugin,"variables");
	}
	
	public void configure() {
		this.mainConfigManager.configure();
		this.playerConfigsManager.configure();
		this.variablesFolderConfigManager.configure();
		configureVariables();
		this.dataConfigManager.configure();
	}

	public void configureVariables(){
		ArrayList<Variable> variables = new ArrayList<>();
		ArrayList<CommonConfig> variablesConfigs = getVariablesConfigs();

		for(CommonConfig customConfig : variablesConfigs){
			FileConfiguration config = customConfig.getConfig();
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
					Limitations limitations = new Limitations();
					if(config.contains(path+".limitations.min_value")){
						limitations.setMinValue(config.getDouble(path+".limitations.min_value"));
					}
					if(config.contains(path+".limitations.max_value")){
						limitations.setMaxValue(config.getDouble(path+".limitations.max_value"));
					}
					if(config.contains(path+".limitations.max_characters")){
						limitations.setMaxCharacters(config.getInt(path+".limitations.max_characters"));
					}
					if(config.contains(path+".limitations.max_decimals")){
						limitations.setMaxDecimals(config.getInt(path+".limitations.max_decimals"));
					}
					if(config.contains(path+".limitations.manage_out_of_range")){
						limitations.setManageOutOfRange(config.getBoolean(path+".limitations.manage_out_of_range"));
					}

					Variable variable = new Variable(key, variableType, valueType, initialValue, possibleValues, limitations);
					variables.add(variable);
				}
			}
		}
		plugin.getVariablesManager().setVariables(variables);
	}

	public PlayersConfigsManager getPlayerConfigsManager() {
		return playerConfigsManager;
	}

	public DataConfigManager getDataConfigManager() {
		return dataConfigManager;
	}

	public MainConfigManager getMainConfigManager() {
		return mainConfigManager;
	}

	private ArrayList<CommonConfig> getVariablesConfigs() {
		ArrayList<CommonConfig> configs = new ArrayList<>();

		configs.add(mainConfigManager.getConfigFile());
		configs.addAll(variablesFolderConfigManager.getConfigs());

		return configs;
	}

	public boolean reloadConfigs(){
		if(!mainConfigManager.reloadConfig()){
			return false;
		}

		configureVariables();

		saveServerData();
		savePlayerData();
		return true;
	}

	public void savePlayerData(){
		if(plugin.getMySQLConnection() == null){
			playerConfigsManager.saveConfigs();
		}
	}

	public void saveServerData(){
		dataConfigManager.saveData();
	}
}
