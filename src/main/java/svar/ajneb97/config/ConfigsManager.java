package svar.ajneb97.config;

import svar.ajneb97.ServerVariables;

public class ConfigsManager {

	private PlayerConfigsManager playerConfigsManager;
	private DataConfigManager dataConfigManager;
	private MainConfigManager mainConfigManager;
	private ServerVariables plugin;
	
	public ConfigsManager(ServerVariables plugin) {
		this.plugin = plugin;
		this.mainConfigManager = new MainConfigManager(plugin);
		this.playerConfigsManager = new PlayerConfigsManager(plugin);
		this.dataConfigManager = new DataConfigManager(plugin);
	}
	
	public void configure() {
		this.mainConfigManager.configure();
		this.playerConfigsManager.configure();
		this.dataConfigManager.configure();
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

	public void reloadConfigs(){
		mainConfigManager.reload();
		saveData();

		dataConfigManager.configure();
		playerConfigsManager.loadPlayers();
	}

	public void saveData(){
		dataConfigManager.saveData();
		playerConfigsManager.savePlayers();
	}
}
