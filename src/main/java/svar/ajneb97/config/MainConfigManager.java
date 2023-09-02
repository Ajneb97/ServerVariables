package svar.ajneb97.config;

import org.bukkit.configuration.file.FileConfiguration;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.managers.MessagesManager;
import svar.ajneb97.model.structure.Limitations;
import svar.ajneb97.model.structure.ValueType;
import svar.ajneb97.model.structure.Variable;
import svar.ajneb97.model.structure.VariableType;
import svar.ajneb97.tasks.DataSaveTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MainConfigManager {

	private ServerVariables plugin;
	private CustomConfig configFile;

	private boolean isMySQL;

	public MainConfigManager(ServerVariables plugin) {
		this.plugin = plugin;
		this.configFile = new CustomConfig("config.yml",plugin,null);
		configFile.registerConfig();
		checkMessagesUpdate();
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

		plugin.getVariablesManager().setVariables(variables);
		plugin.setMessagesManager(new MessagesManager(config.getString("messages.prefix")));

		DataSaveTask dataSaveTask = plugin.getDataSaveTask();
		if(dataSaveTask != null) {
			dataSaveTask.end();
		}
		dataSaveTask = new DataSaveTask(plugin);
		dataSaveTask.start(config.getInt("config.data_save_time"));

		isMySQL = config.getBoolean("config.mysql_database.enabled");
	}

	public CustomConfig getConfigFile() {
		return configFile;
	}

	public boolean isMySQL(){
		return isMySQL;
	}

	public FileConfiguration getConfig(){
		return configFile.getConfig();
	}

	public void saveConfig(){
		configFile.saveConfig();
	}

	public void checkMessagesUpdate(){
		Path pathConfig = Paths.get(configFile.getRoute());
		try{
			String text = new String(Files.readAllBytes(pathConfig));
			if(!text.contains("variableLimitationOutOfRangeMax:")){
				getConfig().set("messages.variableLimitationOutOfRangeMax","&cVariable out of range. Max value is &7%value%");
				getConfig().set("messages.variableLimitationOutOfRangeMin","&cVariable out of range. Min value is &7%value%");
			}
			if(!text.contains("mysql_database:")){
				getConfig().set("config.mysql_database.enabled", false);
				getConfig().set("config.mysql_database.host", "localhost");
				getConfig().set("config.mysql_database.port", 3306);
				getConfig().set("config.mysql_database.username", "root");
				getConfig().set("config.mysql_database.password", "root");
				getConfig().set("config.mysql_database.database", "servervariables");
				saveConfig();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
