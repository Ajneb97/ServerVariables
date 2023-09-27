package svar.ajneb97.config;

import org.bukkit.configuration.file.FileConfiguration;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.model.structure.Limitations;
import svar.ajneb97.model.structure.ValueType;
import svar.ajneb97.model.structure.Variable;
import svar.ajneb97.model.structure.VariableType;

import java.util.ArrayList;
import java.util.List;

public class ConfigsManager {

	private PlayerConfigsManager playerConfigsManager;
	private DataConfigManager dataConfigManager;
	private MainConfigManager mainConfigManager;
	private VariablesFolderConfigManager variablesFolderConfigManager;
	private ServerVariables plugin;
	
	public ConfigsManager(ServerVariables plugin) {
		this.plugin = plugin;
		this.mainConfigManager = new MainConfigManager(plugin);
		this.playerConfigsManager = new PlayerConfigsManager(plugin);
		this.dataConfigManager = new DataConfigManager(plugin);
		this.variablesFolderConfigManager = new VariablesFolderConfigManager(plugin,"variables");
	}
	
	public void configure() {
		this.mainConfigManager.configure();
		this.playerConfigsManager.configure();
		this.dataConfigManager.configure();
		this.variablesFolderConfigManager.configure();
		configureVariables();
	}

	public void configureVariables(){
		ArrayList<Variable> variables = new ArrayList<>();
		ArrayList<CustomConfig> variablesConfigs = getVariablesConfigs();

		for(CustomConfig customConfig : variablesConfigs){
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

					Variable variable = new Variable(key, variableType, valueType, initialValue, possibleValues, limitations);
					variables.add(variable);
				}
			}
		}
		plugin.getVariablesManager().setVariables(variables);
	}

	public PlayerConfigsManager getPlayerConfigsManager() {
		return playerConfigsManager;
	}

	public DataConfigManager getDataConfigManager() {
		return dataConfigManager;
	}

	public MainConfigManager getMainConfigManager() {
		return mainConfigManager;
	}

	private ArrayList<CustomConfig> getVariablesConfigs() {
		ArrayList<CustomConfig> configs = new ArrayList<CustomConfig>();

		configs.add(mainConfigManager.getConfigFile());
		configs.addAll(variablesFolderConfigManager.getConfigs());

		return configs;
	}

	public void reloadConfigs(){
		mainConfigManager.reload();
		variablesFolderConfigManager.reloadConfigs();
		configureVariables();

		saveServerData();
		savePlayerData();
	}

	public void savePlayerData(){
		if(plugin.getMySQLConnection() == null){
			playerConfigsManager.savePlayers();
		}
	}

	public void saveServerData(){
		dataConfigManager.saveData();
	}
}
