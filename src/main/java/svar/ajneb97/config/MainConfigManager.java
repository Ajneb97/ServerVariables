package svar.ajneb97.config;

import org.bukkit.configuration.file.FileConfiguration;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.config.model.CommonConfig;
import svar.ajneb97.managers.MessagesManager;
import svar.ajneb97.tasks.DataSaveTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainConfigManager {

	private ServerVariables plugin;
	private CommonConfig configFile;

	private boolean updateNotify;
	private boolean isMySQL;
	private boolean silentCommandsHideErrors;

	public MainConfigManager(ServerVariables plugin) {
		this.plugin = plugin;
		this.configFile = new CommonConfig("config.yml",plugin,null,false);
		configFile.registerConfig();
		checkMessagesUpdate();
	}
	
	public void configure() {
		FileConfiguration config = configFile.getConfig();

		plugin.setMessagesManager(new MessagesManager(config.getString("messages.prefix")));

		DataSaveTask dataSaveTask = plugin.getDataSaveTask();
		if(dataSaveTask != null) {
			dataSaveTask.end();
		}
		dataSaveTask = new DataSaveTask(plugin);
		dataSaveTask.start(config.getInt("config.data_save_time"));

		updateNotify = config.getBoolean("update_notify");
		isMySQL = config.getBoolean("config.mysql_database.enabled");
		silentCommandsHideErrors = config.getBoolean("config.silent_commands_hide_errors");
	}

	public boolean reloadConfig(){
		if(!configFile.reloadConfig()){
			return false;
		}
		configure();
		return true;
	}

	public CommonConfig getConfigFile() {
		return configFile;
	}

	public boolean isMySQL(){
		return isMySQL;
	}

	public boolean isUpdateNotify() {
		return updateNotify;
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
			if(!text.contains("update_notify:")){
				getConfig().set("config.update_notify",true);
				saveConfig();
			}
			if(!text.contains("verifyServerCertificate:")){
				getConfig().set("config.mysql_database.pool.connectionTimeout",5000);
				getConfig().set("config.mysql_database.advanced.verifyServerCertificate",false);
				getConfig().set("config.mysql_database.advanced.useSSL",true);
				getConfig().set("config.mysql_database.advanced.allowPublicKeyRetrieval",true);
				saveConfig();
			}
			if(!text.contains("silent_commands_hide_errors:")){
				getConfig().set("config.silent_commands_hide_errors",false);
				saveConfig();
			}
			if(!text.contains("commandResetCorrectAll:")){
				getConfig().set("messages.commandResetCorrectAll","&aVariable &7%variable% &areset for &eall players&a.");
				saveConfig();
			}
			if(!text.contains("variableLimitationMaxCharactersError:")){
				getConfig().set("messages.variableLimitationMaxCharactersError","&cVariable supports a maximum of &7%value% &ccharacters.");
				saveConfig();
			}
			if(!text.contains("variableLimitationOutOfRangeMax:")){
				getConfig().set("messages.variableLimitationOutOfRangeMax","&cVariable out of range. Max value is &7%value%");
				getConfig().set("messages.variableLimitationOutOfRangeMin","&cVariable out of range. Min value is &7%value%");
				saveConfig();
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

	public boolean isSilentCommandsHideErrors() {
		return silentCommandsHideErrors;
	}
}
