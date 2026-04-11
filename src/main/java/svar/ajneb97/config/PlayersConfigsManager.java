package svar.ajneb97.config;

import java.util.*;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.config.model.CommonConfig;
import svar.ajneb97.model.ServerVariablesListVariable;
import svar.ajneb97.model.ServerVariablesPlayer;
import svar.ajneb97.model.ServerVariablesStringVariable;
import svar.ajneb97.model.ServerVariablesVariable;
import svar.ajneb97.model.internal.GenericCallback;

public class PlayersConfigsManager extends DataFolderConfigManager{

	public PlayersConfigsManager(ServerVariables plugin, String folderName) {
		super(plugin, folderName);
	}

	@Override
	public void createFiles() {

	}

	@Override
	public void loadConfigs() {
		// No use for player config
	}

	public ServerVariablesPlayer loadConfig(UUID uuid){
		ServerVariablesPlayer playerData = null;
		CommonConfig playerConfig = getConfigFile(uuid+".yml",false);
		if(playerConfig != null){
			// If config exists
			FileConfiguration config = playerConfig.getConfig();
			String name = config.getString("name");

			Map<String,ServerVariablesVariable> variables = new HashMap<>();
			if (config.contains("variables")) {
				for (String key : config.getConfigurationSection("variables").getKeys(false)) {
					if(config.isList("variables."+key)){
						variables.put(key,new ServerVariablesListVariable(key, config.getStringList("variables." + key)));
					}else{
						variables.put(key,new ServerVariablesStringVariable(key, config.getString("variables." + key)));
					}
				}
			}

			playerData = new ServerVariablesPlayer(uuid, name, variables);
		}

		return playerData;
	}

	public void loadConfigAsync(UUID uuid, GenericCallback<ServerVariablesPlayer> callback){
		new BukkitRunnable(){
			@Override
			public void run() {
				ServerVariablesPlayer playerData = null;
				CommonConfig playerConfig = getConfigFile(uuid+".yml",false);
				if(playerConfig != null){
					// If config exists
					FileConfiguration config = playerConfig.getConfig();
					String name = config.getString("name");

					Map<String,ServerVariablesVariable> variables = new HashMap<>();
					if (config.contains("variables")) {
						for (String key : config.getConfigurationSection("variables").getKeys(false)) {
							if(config.isList("variables."+key)){
								variables.put(key,new ServerVariablesListVariable(key, config.getStringList("variables." + key)));
							}else{
								variables.put(key,new ServerVariablesStringVariable(key, config.getString("variables." + key)));
							}
						}
					}

					playerData = new ServerVariablesPlayer(uuid, name, variables);
				}

				ServerVariablesPlayer finalPlayer = playerData;

				new BukkitRunnable(){
					@Override
					public void run() {
						callback.onDone(finalPlayer);
					}
				}.runTask(plugin);
			}
		}.runTaskAsynchronously(plugin);
	}

	public void savePlayer(ServerVariablesPlayer player){
		String playerName = player.getName();
		CommonConfig playerConfig = getConfigFile(player.getUuid()+".yml",true);
		FileConfiguration playerFile = playerConfig.getConfig();

		playerFile.set("name", playerName);
		playerFile.set("variables", null);
		Map<String,ServerVariablesVariable> variables = player.getVariables();
		for(Map.Entry<String, ServerVariablesVariable> entry : variables.entrySet()){
			playerFile.set("variables."+entry.getKey(), entry.getValue().getCurrentValue()); // String or List<String>
		}

		playerConfig.saveConfig();
	}

	@Override
	public void saveConfigs() {
		Map<UUID, ServerVariablesPlayer> players = plugin.getPlayerVariablesManager().getPlayerVariables();

		Map<UUID, ServerVariablesPlayer> playersCopy = new HashMap<>(players);
		for(Map.Entry<UUID, ServerVariablesPlayer> entry : playersCopy.entrySet()){
			ServerVariablesPlayer player = entry.getValue();
			if(player.isModified()){
				savePlayer(player);
			}
			player.setModified(false);
		}
	}

	public void resetDataForAllPlayers(String variableName){
		ArrayList<CommonConfig> configs = getConfigs();
		for(CommonConfig commonConfig : configs) {
			FileConfiguration config = commonConfig.getConfig();

			if(config.contains("variables."+variableName)){
				config.set("variables."+variableName,null);
				commonConfig.saveConfig();
			}
		}
	}
}
